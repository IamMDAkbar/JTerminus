package com.jterminus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility to export the JTerminus application icon as an .ico file
 * This ensures the installer uses the same icon as the application
 */
public class IconExporter {
    
    public static Image createAppIcon() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        // Anti-aliasing for smooth graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Gradient paint: Cyan to Blue
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(0x94E2D5),      // Cyan
            64, 64, new Color(0x89B4FA)     // Blue
        );
        g2.setPaint(gp);
        
        // Draw rounded rectangle background
        g2.fillRoundRect(4, 4, 56, 56, 16, 16);
        
        // Draw terminal prompt symbol ">_"
        g2.setColor(new Color(0x1E1E2E));  // Dark background text
        g2.setFont(new Font("Consolas", Font.BOLD, 30));
        g2.drawString(">_", 12, 44);
        
        g2.dispose();
        return img;
    }
    
    public static void exportIcon(String outputPath) throws IOException {
        System.out.println("Creating JTerminus application icon...");
        
        // Create 256x256 version for better .ico quality
        BufferedImage largeImg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = largeImg.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Gradient paint with larger dimensions
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(0x94E2D5),          // Cyan
            256, 256, new Color(0x89B4FA)       // Blue
        );
        g2.setPaint(gp);
        
        // Draw rounded rectangle background (proportional to size)
        g2.fillRoundRect(16, 16, 224, 224, 64, 64);
        
        // Draw terminal prompt symbol ">_" (proportional to size)
        g2.setColor(new Color(0x1E1E2E));      // Dark background text
        g2.setFont(new Font("Consolas", Font.BOLD, 120));
        FontMetrics fm = g2.getFontMetrics();
        String text = ">_";
        int x = (256 - fm.stringWidth(text)) / 2;
        int y = ((256 - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, x, y);
        
        g2.dispose();
        
        // Save as PNG (ImageIO can't write .ico directly, but we'll save PNG for now)
        String pngPath = outputPath.replace(".ico", ".png");
        ImageIO.write(largeImg, "PNG", new File(pngPath));
        System.out.println("✓ Icon exported: " + pngPath);
        System.out.println("  Use an online PNG to ICO converter or ImageMagick to convert to .ico");
        System.out.println("  Command: magick " + pngPath + " " + outputPath);
    }
    
    public static void main(String[] args) throws IOException {
        String outputPath = args.length > 0 ? args[0] : "jterminus-app.ico";
        exportIcon(outputPath);
    }
}
