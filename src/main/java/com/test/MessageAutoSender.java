package com.test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MessageAutoSender {

    // 定义消息库
    private static final List<String> messageLibrary = List.of(
            "你好！有什么可以帮助的吗？",  // Example message
            "早安！今天的天气怎么样？",
            "谢谢您的帮助！",
            "我爱你！",
            "会见！"
    );

    public static void main(String[] args) throws AWTException, IOException {
        // 随机选择一条消息
        String message = getRandomMessage();
        System.out.println("发送的消息：" + message);

        // 将消息复制到剪贴板
        copyMessageToClipboard(message);

        // 获取当前操作系统
        String os = System.getProperty("os.name").toLowerCase();

        // 根据操作系统调用对应的粘贴方法
        if (os.contains("mac")) {
            // macOS 系统
            pasteMessageOnMac();
        } else if (os.contains("win")) {
            // Windows 系统
            pasteMessageOnWindows();
        } else {
            System.out.println("不支持的操作系统");
        }
    }

    // 从消息库中随机选择一条消息
    private static String getRandomMessage() {
        Random random = new Random();
        return messageLibrary.get(random.nextInt(messageLibrary.size()));
    }

    // 将消息复制到剪贴板
    private static void copyMessageToClipboard(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    // macOS 系统粘贴方法
    private static void pasteMessageOnMac() throws AWTException {
        Robot robot = new Robot();

        // 等待2秒，确保焦点已经在目标输入框
        robot.delay(2000);

        // 模拟按下 Command + V 来粘贴文本（macOS 使用 Command 键）
        robot.keyPress(KeyEvent.VK_META); // 按下 Command 键
        robot.delay(200);
        robot.keyPress(KeyEvent.VK_V);    // 按下 V 键进行粘贴
        robot.keyRelease(KeyEvent.VK_V);  // 释放 V 键
        robot.keyRelease(KeyEvent.VK_META); // 释放 Command 键

        // 模拟按下回车键发送消息
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    // Windows 系统粘贴方法
    private static void pasteMessageOnWindows() throws AWTException {
        Robot robot = new Robot();

        // 等待2秒，确保焦点已经在目标输入框
        robot.delay(2000);

        // 模拟按下 Ctrl + V 来粘贴文本（Windows 使用 Ctrl 键）
        robot.keyPress(KeyEvent.VK_CONTROL); // 按下 Ctrl 键
        robot.delay(200);
        robot.keyPress(KeyEvent.VK_V);       // 按下 V 键进行粘贴

        robot.keyRelease(KeyEvent.VK_V);     // 释放 V 键
        robot.keyRelease(KeyEvent.VK_CONTROL); // 释放 Ctrl 键

        // 模拟按下回车键发送消息
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }
}
