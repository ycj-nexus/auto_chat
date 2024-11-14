package com.core;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class RandomMessageAutoSenderV5 implements NativeKeyListener {

    // 消息文件夹路径，存放每条消息的文本文件
    private static final String MESSAGE_FOLDER_PATH = "messages"; // 消息文件夹路径
    private static List<String> MESSAGE_LIST = new ArrayList<>(); // 消息内容

    private static volatile boolean isRunning = false; // 用于控制程序是否继续运行

    private static Robot robot;
    private static JFrame frame;
    private static JButton startButton;
    private static JLabel instructionLabel;

    public static void main(String[] args) throws AWTException, IOException {
        // 设置 Swing 界面
        SwingUtilities.invokeLater(() -> {
            try {
                robot = new Robot();

                // 创建并设置 JFrame
                frame = new JFrame("自动消息发送器");
                frame.setSize(300, 150);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                startButton = new JButton("开始发送消息");
                instructionLabel = new JLabel("点击按钮开始，按 Shift 键结束程序");

                // 设置按钮点击事件
                startButton.addActionListener(e -> startSendingMessages());

                // 布局设置
                frame.setLayout(new BorderLayout());
                frame.add(instructionLabel, BorderLayout.NORTH);
                frame.add(startButton, BorderLayout.SOUTH);

                frame.setVisible(true);

                // 注册全局键盘监听器
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(new RandomMessageAutoSenderV5());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // 如果按下的是 Shift 键，则结束程序
        if (e.getKeyCode() == NativeKeyEvent.VC_SHIFT) {
            exitProgram();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    private static void startSendingMessages() {
        if (!isRunning) {
            isRunning = true;
            new Thread(() -> {
                try {
                    // 获取操作系统名称
                    String os = System.getProperty("os.name").toLowerCase();

                    // 发送第一条消息
                    String firstMessage = getRandomMessage();
                    System.out.println("发送的第一条消息：" + firstMessage);
                    sendMessage(firstMessage, os);

                    // 随后每隔 3 到 10 分钟发送一条消息
                    Random random = new Random();
                    while (isRunning) {
//                        int interval = random.nextInt(8 * 60 * 1000) + 3 * 60 * 1000; // 3 到 10 分钟之间
                        int interval = 1 * 1000;
                        System.out.println("等待 " + interval / 60000 + " 分钟...");
                        try {
                            Thread.sleep(interval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // 发送一条新的随机消息
                        String message = getRandomMessage();
                        System.out.println("发送的消息：" + message);
                        sendMessage(message, os);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            System.out.println("消息发送开始");
        }
    }

    private static void exitProgram() {
        isRunning = false;
        System.out.println("程序结束");

        // 注销键盘监听器
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 关闭窗口
        frame.dispose();
        System.exit(0);
    }

    /**
     * 从文件夹中随机选择一条消息
     *
     * @return 随机选择的消息
     * @throws IOException 文件读取异常
     */
    private static String getRandomMessage() throws IOException {
        List<String> messageLibrary = loadMessagesFromFolder(MESSAGE_FOLDER_PATH);
        // 确保消息列表不为空
        if (messageLibrary.isEmpty()) {
            throw new IOException("消息文件夹为空，无法加载消息");
        }
        Random random = new Random();
        return messageLibrary.get(random.nextInt(messageLibrary.size()));
    }

    /**
     * 从文件夹中读取所有消息文件内容，并返回一个消息列表。
     *
     * @param folderPath 消息文件夹的路径
     * @return 返回消息列表
     * @throws IOException 文件读取异常
     */
//    private static List<String> loadMessagesFromFolder(String folderPath) throws IOException {
//        List<String> messages = new ArrayList<>();
//        Path folder = Paths.get(folderPath);
//
//        // 确保文件夹存在
//        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
//            throw new IOException("指定的文件夹不存在或不是一个有效的目录：" + folderPath);
//        }
//
//        // 遍历文件夹下的所有文件，假设每个文件包含一条或多条消息
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
//            for (Path entry : stream) {
//                if (Files.isRegularFile(entry) && entry.toString().endsWith(".txt")) {
//                    // 读取每个文件的内容，并将内容作为一条消息添加到列表
//                    String content = new String(Files.readAllBytes(entry), "UTF-8");
//                    messages.add(content);
//                }
//            }
//        }
//        return messages;
//    }

    /**
     * 从文件夹中读取所有消息文件内容，并返回一个消息列表。
     *
     * @param folderPath 消息文件夹的路径
     * @return 返回消息列表
     * @throws IOException 文件读取异常
     */
    private static List<String> loadMessagesFromFolder(String folderPath) throws IOException {
        if (!CollectionUtils.isEmpty(MESSAGE_LIST)){
            return MESSAGE_LIST;
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 获取 "jour_message" 文件夹下的所有 JSON 文件
        Resource[] resources = resolver.getResources(String.format("classpath:%s/*.txt", folderPath));

        for (Resource resource : resources) {
            String fileName = resource.getFilename();
            if (fileName != null && fileName.endsWith(".txt")) {
                try (InputStream inputStream = resource.getInputStream()) {
                    String input = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    // 去除空格和换行符
                    input = input.replaceAll("\\s+", "");
                    System.out.println("读取的消息：" + input);
                    List<String> result = Arrays.asList(input.split(","));
                    MESSAGE_LIST.addAll(result);
                }
            }
        }
        return MESSAGE_LIST;
    }

    /**
     * 将消息复制到系统剪贴板，以便后续粘贴
     *
     * @param message 要复制到剪贴板的消息
     */
    private static void copyMessageToClipboard(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    /**
     * 根据操作系统，模拟按键发送消息
     *
     * @param message 消息内容
     * @param os      操作系统名称
     * @throws AWTException 如果发生异常
     */
    private static void sendMessage(String message, String os) throws AWTException {
        // 将消息复制到剪贴板
        copyMessageToClipboard(message);

        // 根据操作系统调用对应的粘贴方法
        if (os.contains("mac")) {
            pasteMessageOnMac();
        } else if (os.contains("win")) {
            pasteMessageOnWindows();
        } else {
            System.out.println("不支持的操作系统");
        }
    }

    /**
     * macOS 系统粘贴消息的实现方法
     * 使用 Robot 类模拟按下 Command + V 来粘贴剪贴板内容
     * 然后模拟按下回车键发送消息
     *
     * @throws AWTException 如果发生异常
     */
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

    /**
     * Windows 系统粘贴消息的实现方法
     * 使用 Robot 类模拟按下 Ctrl + V 来粘贴剪贴板内容
     * 然后模拟按下回车键发送消息
     *
     * @throws AWTException 如果发生异常
     */
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
