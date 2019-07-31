package com.thedevd.javaexamples.designpatterns.structural;

/* Bridge pattern is about preferring composition over inheritance. 
 * Implementation details of a class are pushed from a hierarchy to 
 * another class with a separate hierarchy.
 * 
 * Realworld example- 
 * Consider you have a website with different pages and you are supposed to allow
 * the user to change the theme. What would you do? 
 * Either Create multiple copies of each of the pages for each of the themes 
 * or would you just create separate theme and load them based on the user's
 * preferences? Bridge pattern allows you to do the second i.e. 
 * 
 * Without Bridge (Preferring Inheritance) -
 * Webpage  ------> Home, About, Career, Contact
 * Home 	------> HomeDark, HomeLight, HomeBlue
 * About	------> AboutDark, AboutLight, AboutBlue
 * Career	------> CareerDark, CareerLight, CareerBlue
 * Contact	------> ContactDark, ContactLight, ContactBlue
 * 
 * With Bridge (preferring composition over Inheritance) -
 * Theme	------> DarkTheme, LightTheme, BlueTheme
 * WebPage	------> Home(Theme), About(Theme), Career(Theme), Contact(Theme) 
 */

public class BridgePattern {

	public static void main( String[] args )
	{
		Theme darkTheme = new DarkTheme();
		Theme lightTheme = new LightTheme();
		Theme blueTheme = new BlueTheme();
		
		Webpage homePage = new Home();
		homePage.applyTheme(lightTheme);
		System.out.println(homePage.getContent());
		
		Webpage aboutPage = new About();
		aboutPage.applyTheme(darkTheme);
		System.out.println(aboutPage.getContent());
		
		Webpage careerPage = new Career();
		careerPage.applyTheme(blueTheme);
		System.out.println(careerPage.getContent());
	}
}

// We have a Theme hierarchy
interface Theme {
	public String getColor();
}

class DarkTheme implements Theme {

	@Override
	public String getColor()
	{
		return "Dark color";
	}
}

class LightTheme implements Theme {

	@Override
	public String getColor()
	{
		return "Light color";
	}
}

class BlueTheme implements Theme {

	@Override
	public String getColor()
	{
		return "Blue color";
	}
}

// We have different Webpages with a separate WebPage hierarchy
// Here we will push implementation details of Theme hierarchy to Webpage hierarchy using composition
interface Webpage {
	public void applyTheme(Theme theme); // THis is composition
	public String getContent();
}

class Home implements Webpage {

	private Theme theme;
	@Override
	public void applyTheme( Theme theme )
	{
		this.theme = theme;
	}

	@Override
	public String getContent()
	{
		return "Home page content in " + theme.getColor();
	}
}

class About implements Webpage {

	private Theme theme;
	@Override
	public void applyTheme( Theme theme )
	{
		this.theme = theme;
	}

	@Override
	public String getContent()
	{
		return "About page content in " + theme.getColor();
	}
}

class Career implements Webpage {

	private Theme theme;
	@Override
	public void applyTheme( Theme theme )
	{
		this.theme = theme;
	}

	@Override
	public String getContent()
	{
		return "Carrer page content in " + theme.getColor();
	}
}

class ContactUs implements Webpage {

	private Theme theme;
	@Override
	public void applyTheme( Theme theme )
	{
		this.theme = theme;
	}

	@Override
	public String getContent()
	{
		return "Contact Us page content in " + theme.getColor();
	}
}

