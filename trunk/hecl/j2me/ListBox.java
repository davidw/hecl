import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ChoiceGroup;

public class ListBox extends Form {
    public ChoiceGroup cg = null;

    public ListBox(String title, int choicetype, String []choices) {
	super(title);
	cg = new ChoiceGroup("", choicetype, choices, null);
	this.append(cg);
    }

    public int append(String item) {
	return cg.append(item, null);
    }
}
