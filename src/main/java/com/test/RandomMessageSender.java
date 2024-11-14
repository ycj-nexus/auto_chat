package com.test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class RandomMessageSender {

    // 定义消息库
    private static final List<String> messageLibrary = List.of(
            "Hello! How can I help you?",
            "Hope you're having a great day!",
            "Let's work together!",
            "Good luck!",
            "Thank you for your support!",
            "The weather is nice today, let's go for a walk?"
    );

    public static void main(String[] args) throws AWTException {
        // 随机选择一条消息
        String message = getRandomMessage();
        System.out.println("发送的消息：" + message);

        // 使用 Robot 模拟键盘输入
        sendMessage(message);
    }

    // 从消息库中随机选择一条消息
    private static String getRandomMessage() {
        Random random = new Random();
        return messageLibrary.get(random.nextInt(messageLibrary.size()));
    }

    // 使用 Robot 模拟键盘输入发送消息
    private static void sendMessage(String message) throws AWTException {
        // 创建 Robot 对象
        Robot robot = new Robot();

        // 等待一些时间，确保焦点已经在需要输入的地方
        robot.delay(2000);  // 2秒钟的延迟，确保你有足够时间将焦点切换到输入框

        // 模拟键盘输入消息
        for (char c : message.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            robot.keyPress(keyCode); // 按下键
            robot.keyRelease(keyCode); // 释放键
            robot.delay(50);  // 每个字符之间稍微延迟，模拟自然输入
        }

        // 模拟按下回车键发送
        robot.keyPress(KeyEvent.VK_ENTER); // 按下回车
        robot.keyRelease(KeyEvent.VK_ENTER); // 释放回车
    }
}
