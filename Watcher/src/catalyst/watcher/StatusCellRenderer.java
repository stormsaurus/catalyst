package catalyst.watcher;


import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.table.DefaultTableCellRenderer;

import catalyst.watcher.Connection.Status;

@SuppressWarnings("serial")
public class StatusCellRenderer extends DefaultTableCellRenderer {

    //private int _width = 12;
    //private int _height = 12;

    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g.create();
        String check = getText();
        Color c = getBackground();
        if( check.equals(""+Status.Connected) ){
            c = new Color(87,237,58);
        } else if( check.equals(""+Status.Error) ){
            c = new Color(252,0,0);
        } else {
            c = Color.DARK_GRAY;
        }
        GradientPaint gp = new GradientPaint(0,0,c,getWidth(),0,getBackground());
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        //g2.fillOval(1, 1, _width, _height);

        //super.paintComponent(g);
        g2.dispose();
    }

}
