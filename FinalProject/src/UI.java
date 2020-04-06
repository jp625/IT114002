
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class UI {
	public static Point testPoint = new Point(0,0);
	public static HashMap<Point, JButton> lazyGrid;
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Grid");
		frame.setLayout(new BorderLayout());
		frame.setSize(new Dimension(800,800));
		//frame.setMinimumSize(new Dimension(600,600));
		//empty panel we'll use as a spacer for now
		JPanel empty = new JPanel();
		int rows = 3;
		int cols = 3;
		Dimension gridDimensions = new Dimension(400,400);
		JPanel grid = new JPanel();
		//set gridlayout pass in rows and cols
		grid.setLayout(new GridLayout(rows, cols));
		grid.setPreferredSize(gridDimensions);
		//grid2.setMaximumSize(gridDimensions);
		JTextField textField = new JTextField();
		//grid layout creation (full layout control)
		for(int i = 0; i < (rows*cols); i++) {
			JButton button = new JButton();
			button.setSize(new Dimension(2,2));
			//convert to x coordinate
			int x = i % rows;
			//convert to y coordinate
			int y = i/cols;
			//%1 first param, %2 second param, etc
			String buttonText = String.format("%1$s:(%2$s, %3$s)", i, x, y);
			//show index and coordinate details on button
			button.setText(buttonText);
			button.setBackground(Color.white);
			//create an action to perform when button is clicked
			//override the default actionPerformed method to tell the code how to handle it
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textField.setText(((JButton)e.getSource()).getText());
					//give focus back to grid for navigation sample
					grid.grabFocus();
				}
				
			});
		}
		//can omit if not doing navigation sample
		lazyGrid = new HashMap<Point,JButton>();
		//keep if using Random, otherwise can omit
		//Random random = new Random();
		int i = 0;
		Dimension buttonSize = new Dimension(2,2);
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < cols; x++) {
				JButton bt = new JButton();
				//%1 first param, %2 second param, etc

				bt.setLocation(x, y);
				//point to button map for easy button reference in navigation sample
				//can omit these related lines if it's not relevant to you
					Point p = new Point(x, y);
					lazyGrid.put(p, bt);
					//set background color based on this point matching our testPoint
					bt.setBackground((p == testPoint)?Color.red:Color.white);
					//uncomment if you want random colors per button
					//bt.setBackground(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));
				//end potential omit section
				bt.setSize(buttonSize);
				//create an action to perform when button is clicked
				//override the default actionPerformed method to tell the code how to handle it
				bt.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//set the textfield value to the text value of the button to show clicked coordinate
						textField.setText(((JButton)e.getSource()).getText());
						//set clicked button to red
						JButton me = ((JButton)e.getSource());
						
						String t = me.getText();
						if(t.equalsIgnoreCase("X")) {
							me.setText("O");
							me.setBackground(Color.gray);
						}
						else {
							me.setText("X");
							me.setBackground(Color.orange);
						}
						
						
						//give focus back to grid2 for navigation sample
						grid.grabFocus();
					}
					
				});
				//add the button to grid2
				grid.add(bt);
				//increment our index to demo the order the buttons are added
				i++;
			}
		}
		//add empty space to prevent the grids from visually merging initially
		frame.add(empty, BorderLayout.CENTER);
		//add grid2 sample to right
		frame.add(grid, BorderLayout.EAST);
		//add output field to bottom
		frame.add(textField, BorderLayout.SOUTH);
		//resize based on elements applied to layout
		frame.pack();
		frame.setVisible(true);
		

	}
}
//Create a move action we can trigger on key press
class MoveAction extends AbstractAction{
	private static final long serialVersionUID = 5137817329873449021L;
	//passed in direction we want to move
	int x,y;
	boolean pressed = false;
	MoveAction(boolean pressed, int x, int y){
		this.x = x;
		this.y = y;
		this.pressed = pressed;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!pressed) {
			//in this example we only care about pressed = true
			//so we return here if it's false (when the key is up)
			return;
		}
		//reset all buttons to white background
		UI.lazyGrid.forEach((point, button)->{
			button.setBackground(Color.white);
		});
		
		//This line passes reference to testPoint, so it doesn't revert correctly
		//when it moves outside of the grid
		//uncomment the below line and comment out line 175 to see
		//Point previous = BasicGrid.testPoint;
		//Point next = previous;
		
		//This creates a new point so we don't affect the original until we want to
		Point previous = new Point(UI.testPoint.x, UI.testPoint.y);
		Point next = new Point(previous.x, previous.y);
		if(x != 0) {
			next.x += x;
		}
		if(y != 0) {
			next.y += y;
		}
		System.out.println("Next Coord: " + next);
		//check if point exists in our grid mapping, if so update the position's color
		if(UI.lazyGrid.containsKey(next)) {
			UI.lazyGrid.get(next).setBackground(Color.red);
			UI.testPoint = next;
		}
		else {
			//reset color for previous point
			UI.lazyGrid.get(previous).setBackground(Color.red);
		}
		System.out.println("TestPoint Coord: " + UI.testPoint);
	}
}
