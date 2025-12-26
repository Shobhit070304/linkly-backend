package com.linkly.backend.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QRCodeService {

    private static final int QR_CODE_SIZE = 300;

    public String generateQRCode(String url) {
        try {
            System.out.println("📱 Generating QR code for: " + url);

            // Create QR code writer
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            // Generate bit matrix
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    url,
                    BarcodeFormat.QR_CODE,
                    QR_CODE_SIZE,
                    QR_CODE_SIZE
            );

            // Convert to image
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convert image to Base64
            String base64QR = convertImageToBase64(qrImage);

            System.out.println("✅ QR code generated successfully");
            return base64QR;

        } catch (WriterException | IOException e) {
            System.err.println("❌ QR code generation failed: " + e.getMessage());
            return null;
        }
    }

    private String convertImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        return "data:image/png;base64," + base64;
    }
}
