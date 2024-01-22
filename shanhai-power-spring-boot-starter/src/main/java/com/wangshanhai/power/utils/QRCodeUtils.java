package com.wangshanhai.power.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * QRCode生成工具类
 */
public class QRCodeUtils {
    /**
     * 二维码BufferedImage对象生成方法
     * @param contents 二维码内容
     * @param width 二维码图片宽度
     * @param height 二维码图片高度
     * @param margin 二维码边框(0,2,4,8)
     */
    public static BufferedImage createQRCode(String contents, int width, int height,int margin) throws Exception {
        if (contents == null || contents.equals("")) {
            throw new Exception("contents不能为空。");
        }
        // 二维码基本参数设置
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, CharacterSetECI.UTF8);// 设置编码字符集utf-8
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);// 设置纠错等级L/M/Q/H,当二维码被损毁一部分时，纠错等级越高，越可能读取成功；同样的，纠错等级越高，单位面积内点阵的点越多，机器扫描时，识别所需时间越长，当前设置等级为最高等级H
        hints.put(EncodeHintType.MARGIN, margin);// 可设置范围为0-10，但仅四个变化0 1(2) 3(4 5 6) 7(8 9 10)
        // 生成图片类型为QRCode
        BarcodeFormat format = BarcodeFormat.QR_CODE;
        // 创建位矩阵对象
        BitMatrix matrix = null;
        try {
            // 生成二维码对应的位矩阵对象
            matrix = new MultiFormatWriter().encode(contents, format, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        // 设置位矩阵转图片的参数
        MatrixToImageConfig config = new MatrixToImageConfig(Color.black.getRGB(), Color.white.getRGB());
        // 位矩阵对象转BufferedImage对象
        BufferedImage qrcode = MatrixToImageWriter.toBufferedImage(matrix, config);
        return qrcode;
    }

    /**
     * 二维码添加LOGO
     * @param qrcode 二维码文件
     * @param width 二维码图片宽度
     * @param height 二维码图片高度
     * @param logoPath  图标LOGO路径
     * @param logoSizeMultiple 二维码与LOGO的大小比例
     */
    public static BufferedImage createQRCodeWithLogo(BufferedImage qrcode,int width, int height, String logoPath, int logoSizeMultiple) throws Exception {
        File logoFile = new File(logoPath);
        if (!logoFile.exists() && !logoFile.isFile()) {
            throw new Exception("指定的LOGO图片路径不存在！");
        }
        try {
            // 读取LOGO
            BufferedImage logo = ImageIO.read(logoFile);
            // 设置LOGO宽高
            int logoHeight = qrcode.getHeight()/logoSizeMultiple;
            int logowidth = qrcode.getWidth()/logoSizeMultiple;
            // 设置放置LOGO的二维码图片起始位置
            int x = (qrcode.getWidth() - logowidth)/2;
            int y = (qrcode.getHeight() - logoHeight)/2;
            // 新建空画板
            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 新建画笔
            Graphics2D g = (Graphics2D) combined.getGraphics();
            // 将二维码绘制到画板
            g.drawImage(qrcode, 0, 0, null);
            // 设置不透明度，完全不透明1f,可设置范围0.0f-1.0f
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            // 绘制LOGO
            g.drawImage(logo, x, y, logowidth, logoHeight, null);
            return combined;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 导出到指定路径
     * @param bufferedImage 二维码文件
     * @param filePath 图片保存路径
     * @param fileName 图片文件名
     * @param formatName 图片格式
     * @return: boolean
     */
    public static boolean generateQRCodeToPath(BufferedImage bufferedImage,String filePath, String fileName, String formatName) {
        // 判断路径是否存在，不存在则创建
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        // 路径后补充斜杠
        if (filePath.lastIndexOf("\\") != filePath.length() - 1) {
            filePath = filePath + "\\";
        }
        // 组合为图片生成的全路径
        String fileFullPath = filePath + fileName + "." + formatName;
        boolean result = false;
        try {
            // 输出图片文件到指定位置
            result = ImageIO.write(bufferedImage, formatName, new File(fileFullPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}