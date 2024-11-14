package com.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/**
 * RandomMessageAutoSenderV5 - 版本4，优化功能
 * 
 * 主要功能：启动后发送第一条消息，随后每隔 3 到 10 分钟随机发送一条消息。
 * 
 * 优化点：
 * 1. 发送完第一条消息后，随机间隔 3 到 10 分钟再发送下一条消息。
 * 2. 消息来源改为从指定文件夹读取所有文本文件，内容可随时通过编辑文件进行更新和修改，管理消息变得更加灵活。
 * 3. 去掉了中英文输入法切换功能，简化了逻辑。
 * 4. 根据操作系统判断选择适合的粘贴方式：macOS 使用 Command + V，Windows 使用 Ctrl + V。
 * 
 * 使用方法：
 * 1. 在程序运行的根目录下创建一个 `messages` 文件夹，里面放置 `.txt` 文件，每个文件包含一条消息或多条消息。
 * 2. 运行程序时，它将随机选择一条消息，并自动将消息粘贴到当前焦点所在的输入框。接着每隔 3 到 10 分钟发送一条新的随机消息。
 */
public class RandomMessageAutoSenderV4 {

    // 消息文件夹路径，存放每条消息的文本文件
    private static final String MESSAGE_FOLDER_PATH = "messages"; // 消息文件夹路径

    public static void main(String[] args) throws AWTException, IOException {
        // 获取操作系统名称
        String os = System.getProperty("os.name").toLowerCase();

        // 发送第一条消息
        String firstMessage = getRandomMessage();
        System.out.println("发送的第一条消息：" + firstMessage);
        sendMessage(firstMessage, os);

        // 随后每隔 3 到 10 分钟发送一条消息
        Random random = new Random();
        while (true) {
            // 随机生成 3 到 10 分钟之间的间隔，单位为毫秒
            int interval = random.nextInt(8 * 60 * 1000) + 3 * 60 * 1000; // 3 到 10 分钟之间
//            int interval = 1 * 1000;
            // 打印等待时间的日志
            System.out.println("等待 " + interval / 60000 + " 分钟...");
            try {
                // 等待随机时间间隔
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 发送一条新的随机消息
            String message = getRandomMessage();
            System.out.println("发送的消息：" + message);
            sendMessage(message, os);
        }
    }

    /**
     * 从文件夹中读取所有消息文件内容，并返回一个消息列表。
     * 
     * @param folderPath 消息文件夹的路径
     * @return 返回消息列表
     * @throws IOException 文件读取异常
     */
    private static List<String> loadMessagesFromFolderPath(String folderPath) throws IOException {
        List<String> messages = new ArrayList<>();
        Path folder = Paths.get(folderPath);

        // 确保文件夹存在
        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            throw new IOException("指定的文件夹不存在或不是一个有效的目录：" + folderPath);
        }

        // 遍历文件夹下的所有文件，假设每个文件包含一条或多条消息
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry) && entry.toString().endsWith(".txt")) {
                    // 读取每个文件的内容，并将内容作为一条消息添加到列表
                    String content = new String(Files.readAllBytes(entry), "UTF-8");
                    messages.add(content);
                }
            }
        }
        return messages;
    }

    // 读取指定文件夹下所有的 JSON 文件内容并解析
    private static List<String> loadMessagesFromFolder(String folderPath) throws IOException {
        List<String> messages = new ArrayList<>();

        // 获取 resources 文件夹下的文件路径
        ClassLoader classLoader = RandomMessageAutoSenderV4.class.getClassLoader();
        File folder = new File(classLoader.getResource(folderPath).getFile());

        // 获取文件夹中的所有 .txt 文件
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            throw new IOException("文件夹中没有 .txt 文件，或读取失败：" + folderPath);
        }

        // 逐个读取文件中的内容
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder fileContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line);  // 拼接文件内容
                }

                // 解析 JSON 内容
                JSONObject jsonObject = JSONObject.parseObject(fileContent.toString());
                JSONArray jsonMessages = jsonObject.getJSONArray("messages");

                for (int i = 0; i < jsonMessages.size(); i++) {
                    messages.add(jsonMessages.getString(i));  // 获取每一条消息
                }
            } catch (IOException e) {
                System.err.println("读取文件 " + file.getName() + " 时发生错误: " + e.getMessage());
            }
        }

        return messages;
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
     * @param os 操作系统名称
     * @throws AWTException 如果发生异常
     */
    private static void sendMessage(String message, String os) throws AWTException {
        // 将消息复制到剪贴板
        copyMessageToClipboard(message);

        // 根据操作系统调用对应的粘贴方法
        if (os.contains("mac")) {
            // macOS 系统，直接粘贴消息
            pasteMessageOnMac();
        } else if (os.contains("win")) {
            // Windows 系统，直接粘贴消息
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
