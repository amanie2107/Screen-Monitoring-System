/*
	File Name 	: blank_screen.java
	Solution 	: detect blank screen in video
	Authour		: Anoop Chandra , Kiran

*/
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.Scanner;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Date;
import java.sql.Timestamp;

class Screen
{
	private static Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();
	private static Rectangle size = new Rectangle(dimensions);
	private static Robot maggie;
	public static int screen_color;
	private static int[] blank_color;
	public  static String[] blank_color_name;

	static
	{
		try
		{
			maggie = new Robot();
		}

		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		//The elements are integer rgb values
		blank_color = new int[]{-16777216,-16735256,-3947581};
		blank_color_name = new String[]{"black","blue","grey"};
	}
	//Linear search, returns true if value of color argument matches any value in blank_colors array
	private static boolean find_color(int color)
	{
		for(int i = 0;i < blank_color.length;i++)
		{
			if(blank_color[i] == color)
			{
				screen_color = i;

				return true;
			}
		}

		return false;
	}
	//Resizong the captured image.
	//returns scaled version of argument img with dimensions resized by the argument factor
	private static BufferedImage resize_image(BufferedImage img,double factor)
	{
		int height = (int)(Screen.dimensions.height * factor);
		int width = (int)(Screen.dimensions.width * factor);

		BufferedImage scaled_img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

		Graphics g = scaled_img.createGraphics();
		g.drawImage(img,0,0,Screen.dimensions.width,Screen.dimensions.height,null);

		return scaled_img;
	}

		public static boolean is_blank()
	{
		try
		{
			//getting current screen state
			BufferedImage img = Screen.maggie.createScreenCapture(Screen.size);
			//Compressing the image by halving the dimensions
			img = resize_image(img,0.5);
			//Color of the Top left pixel
			int color = img.getRGB(0,0);
			//checking for selected blank pixel color
			if(!find_color(color))
			{
				return false;
			}

			int x,y,h1 = img.getHeight() / 3,h2 = h1 * 2;
			// Traversing diagonal pixels and 2 horizontal lines at equal intervals
			for(x = 1;x < img.getWidth();x++)
			{
				y = x % img.getHeight();
				//Checking for heterogeneous color in the diagonal pixel and the horizontal lines at equal intervals
				if(color != img.getRGB(x,y) || color != img.getRGB(x,h1) || color != img.getRGB(x,h2))
				{
					return false;
				}

			}


			return true;
		}

		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		return false;
	}

}

class program
{
	public static void main(String args[])		//the parameters are color(String), number of minutes/notification(Int) and number of seconds in each interval(Int)
	{
		if(args.length != 2)
		{
			System.out.println("Please number of minutes for notification and number of seconds in each interval");
			System.exit(0);
		}

		int blank_count = 0,interval,n;
		interval = Integer.parseInt(args[1]);
		n = Integer.parseInt(args[0]) * 60 / interval;
		interval *= 1000;



		while(true)
		{
			if(Screen.is_blank())
			{
				java.util.Date date= new java.util.Date();
	 			System.out.println(new Timestamp(date.getTime()));

				blank_count++;

				if(blank_count == n)	//in case the screen is judged to be blank n contiguos iterations, send notification
				{
							System.out.println(Screen.blank_color_name[Screen.screen_color]+"Detected");
							blank_count = 0;
				}
			}

			else
			{
				blank_count = 0;
			}

			try
			{
				Thread.sleep(interval);		//Halt execution
			}

			catch(InterruptedException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
