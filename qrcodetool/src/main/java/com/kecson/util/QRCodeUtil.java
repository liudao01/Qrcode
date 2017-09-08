package com.kecson.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;


public class QRCodeUtil {
    private static final String CHARSET_UTF_8 = "utf-8";
    private static final String FORMAT_NAME = "jpg";
    private static final int QRCODE_SIZE = 320;
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;

    /**
     * 生成二维码
     *
     * @param content 二维码内容
     *
     * @return 二维码BufferedImage
     *
     * @throws Exception
     */
    public static BufferedImage encode(String content) throws Exception {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET_UTF_8);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, 1);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
//                image.setRGB(x, y, bitMatrix.get(x, y) ? -16777216 : -1);
                image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK.getRGB() : WHITE.getRGB());
            }
        }
        return image;
    }

    /**
     * 并保存到指定路径
     *
     * @param content       二维码内容
     * @param saveImagePath 保存目录或者保存路径
     *
     * @throws Exception
     */
    public static void encode(String content, String saveImagePath) throws Exception {
        BufferedImage image = encode(content);
        File file = new File(saveImagePath);
        File imageFile;
        if (file.isDirectory()) {
            String now = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS").format(System.currentTimeMillis());
            imageFile = new File(file, now + ".jpg");
        } else if (file.exists()) {
            String now = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS").format(System.currentTimeMillis());
            imageFile = new File(file.getParentFile(), now + ".jpg");
        } else {
            imageFile = file;
        }

        ImageIO.write(image, FORMAT_NAME, imageFile);
    }

    public static void encode(String content, String dir, String fileName) throws Exception {
        BufferedImage image = encode(content);
        File file = new File(dir, fileName);
        File imageFile;
        if (file.isDirectory()) {
            String now = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS_SSS").format(System.currentTimeMillis());
            imageFile = new File(file, now + ".jpg");
        } else if (file.exists()) {
            String now = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS").format(System.currentTimeMillis());
            imageFile = new File(file.getParentFile(), now + ".jpg");
        } else {
            imageFile = file;
        }
        imageFile.mkdirs();
        ImageIO.write(image, FORMAT_NAME, imageFile);
        System.out.println("二维码图片路径:\n" +
                                   imageFile.getAbsolutePath());
    }

    public static void encodeWithLogo(String content, String dir, String fileName, String logoPath) throws Exception {
        BufferedImage image = encode(content);
        image = addLogo(image, logoPath);

        File file = new File(dir, fileName);
        File imageFile;
        if (file.isDirectory()) {
            String now = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS").format(System.currentTimeMillis());
            imageFile = new File(file, now + ".jpg");
        } else if (file.exists()) {
            String now = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS").format(System.currentTimeMillis());
            imageFile = new File(file.getParentFile(), now + ".jpg");
        } else {
            imageFile = file;
        }

        imageFile.mkdirs();
        ImageIO.write(image, FORMAT_NAME, imageFile);
        System.out.println("二维码图片路径:\n" +
                                   imageFile.getAbsolutePath());
    }

    /**
     * 生成二维码并保存到指定输出流
     *
     * @param content 内容
     * @param output  输出流
     *
     * @throws Exception
     */
    public static void encode(String content, OutputStream output) throws Exception {
        BufferedImage image = encode(content);
        ImageIO.write(image, "jpg", output);
    }


    /**
     * 二维码中间加Logo
     *
     * @param qrCodeSource 二维码Image
     * @param logoPath     Logo文件名
     *
     * @return
     *
     * @throws Exception
     */
    public static BufferedImage addLogo(BufferedImage qrCodeSource, String logoPath) throws Exception {
        File file = new File(logoPath);
        if (!file.exists()) {
            System.err.println("[" + logoPath + "]   Logo文件不存在！");
            return qrCodeSource;
        }
        Image src = ImageIO.read(file);
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (width > WIDTH) {
            width = WIDTH;
        }
        if (height > HEIGHT) {
            height = HEIGHT;
        }
        Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        g.drawImage(image, 0, 0, null); // 绘制缩小后的图
        g.dispose();
        src = image;
        // 插入LOGO
        Graphics2D graph = qrCodeSource.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();

        return qrCodeSource;

    }

/* *****************************************
 *           解析二维码
 *******************************************/

    /**
     * 解析二维码
     *
     * @param file 二维码文件
     *
     * @return 二维码内容
     *
     * @throws Exception
     */
    public static String decode(File file)
            throws Exception {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Hashtable hints = new Hashtable();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET_UTF_8);
        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    /**
     * 解析二维码
     *
     * @param path 二维码文件路径
     *
     * @return 二维码内容
     *
     * @throws Exception
     */
    public static String decode(String path) throws Exception {
        return decode(new File(path));
    }
}
