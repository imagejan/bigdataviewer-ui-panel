package bdv.ui.panel;

import java.awt.Color;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.imagej.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.RealType;


public class Main {
	public static void main(String... args) {
		String filename = readCommandline(args);
		if (filename == null)
			filename = showOpenDialog();
		if (filename != null)
			start(filename);
	}

	private static String readCommandline(String[] args) {
		switch (args.length) {
		case 1:
			return args[0];
		case 0:
			return null;
		default:
			System.err.println("USAGE:    ./start.sh {optional/path/to/image.tif}");
			System.exit(2);
		}
		throw new AssertionError();
	}

	private static String showOpenDialog() {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(true);

		final int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return null;
		return fileChooser.getSelectedFile().getAbsolutePath();
	}

	public static <T extends RealType<T>> void start(String filename) {
		ImageJ imageJ = new ImageJ();
		imageJ.ui().showUI();
		RandomAccessibleInterval<T> img = null;
		try {
			 img = (RandomAccessibleInterval<T>)imageJ.io().open(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JFrame frame = new JFrame("BDV-UI");
		frame.setBounds(50, 50, 1200, 900);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
		BigDataViewerUI bdvUI = new BigDataViewerUI<>(frame, imageJ.context());
		
		frame.getContentPane().add(bdvUI.getPanel());
		frame.pack();
//		imageJ.ui().show(img);
		if (img != null) {
			Set<String> groups = new HashSet<>();
			groups.add(filename);
			bdvUI.addImage(img, img.randomAccess().get().createVariable().toString(), filename, true, 
					groups, Color.WHITE, new AffineTransform3D(), Double.NaN, Double.NaN);
		}
	}
}
