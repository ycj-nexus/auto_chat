package com.test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class RandomMessageSenderV2 {

    // 定义消息库
    private static final List<String> messageLibrary = List.of(
            // 生活类
            "主播晚上好",
            "主播晚上好，今天准备贴多少背板",  // Example message
            "主播今天这么早",
            "主播今天东西多吗",
            "主播吃饭了吗",
            "主播，今天吃什么？",
            "主播，这个在家也能做吗",
            "今天打算播到几点",
            // 产品类
            "主播，这种双面胶的粘性怎么样？能承受户外环境的考验吗",
            "请问这种背板材料是什么？有什么特殊功能吗",
            "主播，贴胶的过程中有什么技巧可以分享吗",
            "这种背板的耐候性如何？在极端天气下表现怎么样",
            "主播，有没有推荐的双面胶品牌？我也想做这个。",
            "我之前在贴胶时遇到过贴错撕下来困难的问题，主播是怎么解决的",
            "主播贴胶的手法看起来很专业，学习了",
            "这个直播间真的很实用，谢谢主播的分享，让我学到了很多",
            "主播，今天的直播速度适中，信息量刚刚好",
            "如果主播能展示一下背板的防水性能，那就更完美了",
            "主播辛苦了，今天的贴胶技巧很有帮助，期待下次直播"
    );

    public static void main(String[] args) throws AWTException, IOException {
        // 随机选择一条消息
        String message = getRandomMessage();
        System.out.println("发送的消息：" + message);

        // 将消息复制到剪贴板
        copyToClipboard(message);

        // 使用 Robot 模拟粘贴操作
        pasteMessageFromClipboard();
    }

    // 从消息库中随机选择一条消息
    private static String getRandomMessage() {
        Random random = new Random();
        return messageLibrary.get(random.nextInt(messageLibrary.size()));
    }

    // 将消息复制到剪贴板
    private static void copyToClipboard(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    // 使用 Robot 模拟粘贴操作（Ctrl + V）
    private static void pasteMessageFromClipboard() throws AWTException {
        Robot robot = new Robot();

        // 等待2秒，确保焦点已经在目标输入框
        robot.delay(2000);  // 2秒钟的延迟，确保你有足够时间将焦点切换到输入框

        // 模拟按下 Ctrl + V 来粘贴文本
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V); // 按下 V 键进行粘贴
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        // 模拟按下回车键发送消息
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }
}
