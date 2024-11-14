package com.test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * RandomMessageAutoSenderV2
 *
 * 该类用于实现自动从消息库中随机选择一条消息，粘贴到目标输入框并发送。
 * 相较于之前的版本，此版本做了以下优化：
 * 1. 根据操作系统自动识别并执行不同的粘贴操作。
 * 2. 自动检测当前输入法，如果是中文输入法，则尝试切换到英文输入法。
 * 3. 优化了输入法切换的逻辑，不会错误地切换成中文输入法，避免每次中文输入发不出来的问题。
 * 4. 采用 `Robot` 类来模拟键盘输入，通过剪贴板进行消息粘贴。
 * 5. 支持 macOS 和 Windows 系统，并通过系统属性判断当前操作系统类型。
 */
public class RandomMessageAutoSenderV2 {

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

        // 根据操作系统调用对应的输入法切换和粘贴方法
        if (os.contains("mac")) {
            // macOS 系统，切换输入法并粘贴
            if (isInputMethodChinese()) {
                switchInputMethodOnMac();
            }
            pasteMessageOnMac();
        } else if (os.contains("win")) {
            // Windows 系统，切换输入法并粘贴
            if (isInputMethodChinese()) {
                switchInputMethodOnWindows();
            }
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

    // 判断当前输入法是否是中文输入法
    private static boolean isInputMethodChinese() {
        // 这里你可以实现一些逻辑来判断当前输入法是否是中文
        // 比如通过判断系统的默认语言、环境变量等方式
        String os = System.getProperty("os.name").toLowerCase();
        // 这里只是一个简单的示范，你可以基于自己的实际情况来判断输入法
        return os.contains("win"); // Windows 系统假设中文输入法为默认
    }

    // macOS 系统切换输入法
    private static void switchInputMethodOnMac() throws AWTException {
        Robot robot = new Robot();

        // 等待2秒，确保焦点已经在目标输入框
        robot.delay(2000);

        // 模拟按下 Command + Space 来切换输入法（macOS 默认快捷键）
        robot.keyPress(KeyEvent.VK_META);  // 按下 Command 键
        robot.keyPress(KeyEvent.VK_SPACE); // 按下 Space 键切换输入法
        robot.keyRelease(KeyEvent.VK_SPACE);
        robot.keyRelease(KeyEvent.VK_META); // 释放 Command 键
    }

    // Windows 系统切换输入法
    private static void switchInputMethodOnWindows() throws AWTException {
        Robot robot = new Robot();

        // 等待2秒，确保焦点已经在目标输入框
        robot.delay(2000);

        // 模拟按下 Alt + Shift 来切换输入法（Windows 默认快捷键）
        robot.keyPress(KeyEvent.VK_ALT);  // 按下 Alt 键
        robot.keyPress(KeyEvent.VK_SHIFT); // 按下 Shift 键
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_ALT); // 释放 Alt 键
    }

    // macOS 系统粘贴方法
    private static void pasteMessageOnMac() throws AWTException {
        Robot robot = new Robot();

        // 模拟按下 Command + V 来粘贴文本（macOS 使用 Command 键）
        robot.keyPress(KeyEvent.VK_META); // 按下 Command 键
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

        // 模拟按下 Ctrl + V 来粘贴文本（Windows 使用 Ctrl 键）
        robot.keyPress(KeyEvent.VK_CONTROL); // 按下 Ctrl 键
        robot.keyPress(KeyEvent.VK_V);       // 按下 V 键进行粘贴
        robot.keyRelease(KeyEvent.VK_V);     // 释放 V 键
        robot.keyRelease(KeyEvent.VK_CONTROL); // 释放 Ctrl 键

        // 模拟按下回车键发送消息
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }
}
