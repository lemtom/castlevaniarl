package sz.gadgets;

import sz.csi.textcomponents.MenuItem;

import java.awt.*;

public interface GFXMenuItem extends MenuItem{
	Image getMenuImage();
	String getMenuDescription();
	String getMenuDetail();

}