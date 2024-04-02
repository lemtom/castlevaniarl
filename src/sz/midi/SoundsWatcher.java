package sz.midi;


//SoundsWatcher.java
//Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* 
*/

public interface SoundsWatcher 
{
// constants used by SoundsWatcher method, atSequenceEnd()
int STOPPED = 0;
int REPLAYED = 1;

void atSequenceEnd(String filename, int status);
}
