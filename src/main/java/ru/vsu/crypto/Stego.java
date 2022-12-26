package ru.vsu.crypto;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Stego {

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("src/main/resources/pretty-cat.png"));
        String text = "I very like stenography, because it is very interesting";
        System.out.println("Need to hide this message: " + text);
        Stego stego = new Stego();
        BufferedImage encoded = stego.encode(text.getBytes(StandardCharsets.UTF_8), image);
        File outputfile = new File("src/main/resources/pretty-cat-encoded.png");
        ImageIO.write(encoded, "png", outputfile);

        byte[] decodedResult = stego.decode(encoded, text.length());
        System.out.println("Decoded massage: " + new String(decodedResult, StandardCharsets.UTF_8));
    }

    public BufferedImage encode(byte[] information, BufferedImage image) {
        Generator generator = new Generator();
        BufferedImage resultImage = makeImageCopy(image);
        if (resultImage.getHeight() * resultImage.getWidth() < information.length * 8) {
            throw new IllegalArgumentException("This picture is too small");
        }
        for (byte value : information) {
            for (int b = 0; b < 7; ) {
                int x = (int) (generator.random() * resultImage.getWidth());
                int y = (int) (generator.random() * resultImage.getHeight());
                Color pixel = new Color(resultImage.getRGB(x, y));

                int R = (b == 0) ? pixel.getRed() & 254 : pixel.getRed() & 254 | getBit(value, b++);
                int G = pixel.getGreen() & 254 | getBit(value, b++);
                int B = pixel.getBlue() & 254 | getBit(value, b++);
                Color newPixel = new Color(R, G, B);
                resultImage.setRGB(x, y, newPixel.getRGB());
            }
        }
        return resultImage;
    }

    public byte[] decode(BufferedImage image, int messageSize) {
        Generator generator = new Generator();
        int textLength = 0;
        byte[] information = new byte[messageSize];

        StringBuilder tmp = new StringBuilder();

        while (textLength != messageSize) {
            for (int b = 0; b < 7; ) {
                int x = (int) (generator.random() * image.getWidth());
                int y = (int) (generator.random() * image.getHeight());

                Color pixel = new Color(image.getRGB(x, y));
                if (b != 0) {
                    tmp.append(pixel.getRed() % 2);
                    b++;
                }
                tmp.append(pixel.getGreen() % 2);
                b++;
                tmp.append(pixel.getBlue() % 2);
                b++;
            }
            int byteInt = Integer.parseInt(tmp.reverse().toString(), 2);
            information[textLength] = ((byte) byteInt);
            tmp = new StringBuilder();
            textLength++;
        }

        return information;
    }

    private byte getBit(byte val, int pos) {

        return (byte) ((val & (1 << pos)) >> pos);
    }

    private BufferedImage makeImageCopy(BufferedImage imageToCopy) {
        BufferedImage result = new BufferedImage(imageToCopy.getWidth(), imageToCopy.getHeight(), imageToCopy.getType());
        Graphics g = result.getGraphics();
        g.drawImage(imageToCopy, 0, 0, null);
        return result;
    }

}
