package hci;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.ArrayList;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import hci.utils.Point;

/**
 * Main class of the program - handles display of the main window
 * @author Michal
 *
 */
public class ImageLabeller extends JFrame {
	/**
	 * Image Variables for Saving
	 */	
  private BufferedImage bufferedImageSrc = null;
  private static BufferedImage bufferedImage = null;
  private static ArrayList<Point> currentPolygon = null;
  private static ArrayList<ArrayList<Point>> polygonsList = null;

	/**
	 * some java stuff to get rid of warnings
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * main window panel
	 */
	JPanel appPanel = null;
	
	/**
	 * toolbox - put all buttons and stuff in these two toolboxes
	 */
	JPanel topToolboxPanel = null;
	JPanel bottomToolboxPanel = null;
	JPanel rightToolboxPanel = null;
	
	/**
	 * image panel - displays image and editing area
	 */
	private static ImagePanel imagePanel = new ImagePanel();

	/**
	 * middle panel - holds image and middletoolbox panels
	 */
	JPanel middlePanel = null;
	
	/**
	 * Launches file choose to retrieve an image path
	*/
	public void launchFileChooser(){
		String imagePath = null;
		JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "JPG & GIF Images", "jpg", "gif");
    chooser.setFileFilter(filter);
    int returnVal = chooser.showOpenDialog(this);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
        imagePath = chooser.getSelectedFile().getAbsolutePath().toString();
    }
    imagePanel.setImage(imagePath);

    // Setting Buffer Image variables to perform Save later
    try {
          bufferedImageSrc = ImageIO.read(new File(imagePath));
    			bufferedImage = toBufferedImage(bufferedImageSrc);
        } catch(IOException e) {
            System.out.println("Error Buffering Image");
        }
	}

	/**
	 * Saving functionality
	 */
	private static void saveLabelledImage(String ext) {
        String fileName = "savingAnImage";
        //Creates a new directory to store Image and Polygon Coordinates
        boolean dirSuccess = (new File("./"+fileName)).mkdirs();
        String filePath = "./"+fileName+"/"+fileName;
        File file = new File(filePath + "." + ext);
        try {
            ImageIO.write(bufferedImage, ext, file);  // ignore returned boolean
            System.out.println("Saving File to: " + file.getPath());
            polygonsList = imagePanel.getPolygonsList();
        } catch(IOException e) {
            System.out.println("Write error for " + file.getPath() +
                               ": " + e.getMessage());
        }
    }
 
    private static BufferedImage toBufferedImage(Image src) {
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        int type = BufferedImage.TYPE_INT_RGB;  // other options
        BufferedImage dest = new BufferedImage(w, h, type);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return dest;
    }

	/**
	 * handles New Object button action
	 */
	public void addNewPolygon() {
		imagePanel.addNewPolygon();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		imagePanel.paint(g); //update image panel
	}
	
	/**
	 * sets up application window
	 * @param imageFilename image to be loaded for editing
	 * @throws Exception
	 */
	public void setupGUI() throws Exception {
		this.addWindowListener(new WindowAdapter() {
		  	public void windowClosing(WindowEvent event) {
		  		//here we exit the program (maybe we should ask if the user really wants to do it?)
		  		//maybe we also want to store the polygons somewhere? and read them next time
		  		System.out.println("Bye bye!");
		    	System.exit(0);
		  	}
		});

		//setup main window panel
		appPanel = new JPanel();
		appPanel.setLayout(new BoxLayout(appPanel, BoxLayout.Y_AXIS));
		this.setLayout(new BoxLayout(appPanel, BoxLayout.Y_AXIS));
		this.setContentPane(appPanel);

 
		//create top toolbox panel
        topToolboxPanel = new JPanel();
        
        //Add button
		JButton randbutton = new JButton("exit");
		randbutton.setMnemonic(KeyEvent.VK_N);
		randbutton.setSize(50, 20);
		randbutton.setEnabled(true);
		randbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    	System.exit(1);
			}
		});
		randbutton.setToolTipText("Click to exit");
		
		topToolboxPanel.add(randbutton);
		
		//add toolbox to window
		appPanel.add(topToolboxPanel);
		

		//create middle panel with image and sidebar
		middlePanel = new JPanel();

        //Create and set up the image panel.
		imagePanel = new ImagePanel();
		imagePanel.setOpaque(true); //content panes must be opaque

		middlePanel.add(imagePanel);

		rightToolboxPanel = new JPanel();
		
        //Add button
		JButton openImageButton = new JButton("Open Image");
		openImageButton.setMnemonic(KeyEvent.VK_N);
		openImageButton.setSize(50, 20);
		openImageButton.setEnabled(true);
		openImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    	launchFileChooser();
			}
		});
		openImageButton.setToolTipText("Click to open a new image");

		JButton saveLabelledImageButton = new JButton("Save");
		saveLabelledImageButton.setMnemonic(KeyEvent.VK_N);
		saveLabelledImageButton.setSize(50, 20);
		saveLabelledImageButton.setEnabled(true);
		saveLabelledImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    	saveLabelledImage("jpg");
			}
		});
		saveLabelledImageButton.setToolTipText("Click to save labelled image");

		JButton newPolyButton = new JButton("New object");
		newPolyButton.setMnemonic(KeyEvent.VK_N);
		newPolyButton.setSize(50, 20);
		newPolyButton.setEnabled(true);
		newPolyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    	addNewPolygon();
			}
		});
		newPolyButton.setToolTipText("Click to add new object");
		
		rightToolboxPanel.add(saveLabelledImageButton);
		rightToolboxPanel.add(openImageButton);
		rightToolboxPanel.add(newPolyButton);
		
		//add toolbox to window
		middlePanel.add(rightToolboxPanel);

        appPanel.add(middlePanel);


        //create toolbox panel
        bottomToolboxPanel = new JPanel();
        
        //Add button
		JButton editLabelButton = new JButton("Edit Label");
		editLabelButton.setMnemonic(KeyEvent.VK_N);
		editLabelButton.setSize(50, 20);
		editLabelButton.setEnabled(true);
		editLabelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    	addNewPolygon();
			}
		});
		editLabelButton.setToolTipText("Click to edit a label");
		
		bottomToolboxPanel.add(editLabelButton);
		
		//add toolbox to window
		appPanel.add(bottomToolboxPanel);
		
		//display all the stuff
		this.pack();
        this.setVisible(true);
	}
	
	/**
	 * Runs the program
	 * @param argv path to an image
	 */
	public static void main(String argv[]) {
		try {
			//create a window and display the image
			ImageLabeller window = new ImageLabeller();
			window.setupGUI();
		} catch (Exception e) {
			System.err.println("Image: ");
			e.printStackTrace();
		}
	}
}
