package com.maxrun.application.common.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageCompressUtil {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int iHeight=0;
		int iWidth=0;
		
		File f = new File("C:\\SD\\GIT-REPO\\photoapp\\src\\main\\webapp\\resources\\photo\\12\\2023\\11\\3468\\125.jpg");
		
		BufferedImage og = ImageIO.read(f);
		
		iHeight = og.getHeight();
		iWidth = og.getWidth();
		long size = f.length();
		
		System.out.println("height===>" + String.valueOf(iHeight));
		System.out.println("height===>" + String.valueOf(iWidth));
		System.out.println("size===>" + String.valueOf(size));
		
		BufferedImage rsi = resizeImage(og, iWidth, iHeight);
		
		File outputfile = new File("C:\\SD\\GIT-REPO\\photoapp\\src\\main\\webapp\\resources\\photo\\12\\2023\\11\\3468\\rsi_125.jpg");
		ImageIO.write(rsi, "jpg", outputfile);
		
		iHeight = rsi.getHeight();
		iWidth = rsi.getWidth();
		size = outputfile.length();
		
		System.out.println("height===>" + String.valueOf(iHeight));
		System.out.println("height===>" + String.valueOf(iWidth));
		System.out.println("size===>" + String.valueOf(size));
		
		
	}
	
	public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
	    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics2D = resizedImage.createGraphics();
	    graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
	    graphics2D.dispose();
	    return resizedImage;
	}

}
