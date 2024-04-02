package sz.gadgets;

import java.awt.Image;

import sz.csi.textcomponents.MenuItem;

public interface GFXMenuItem extends MenuItem{
	Image getMenuImage();
	String getMenuDescription();
	String getMenuDetail();

}