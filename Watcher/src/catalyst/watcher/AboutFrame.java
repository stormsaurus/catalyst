package catalyst.watcher;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang.time.DateFormatUtils;

@SuppressWarnings("serial")
public class AboutFrame extends JFrame implements ActionListener{

    public AboutFrame(){
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        getContentPane().setLayout(gridbag);
        c.insets = new Insets(5,5,5,5);

        JTextArea text = new JTextArea();
        text.setColumns(80);
        text.setRows(10);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.append("Catalyst\n");
        text.append("Copyright (c)"+DateFormatUtils.format(new Date(), "yyyy")+".  All rights reserved.\n");
        text.append("\n");
        text.append("Crystal Project Icons\n");
        text.append("Everaldo Coelho (http://www.everaldo.com/)\n");
        text.append("Copyright (c) 2006-2007. Licensed LGPL\n");
        text.append("\n");
        text.append("Baby seal, noob, and lolcat safe.\n");
        JScrollPane scroller = new JScrollPane(text);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        c.fill                                 = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        getContentPane().add(scroller, c);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(this);
        c.fill                                 = GridBagConstraints.NONE;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        getContentPane().add(okButton,c);

        setTitle("About");

        pack();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation(screenSize.width/2-getWidth()/2, screenSize.height/2-getHeight()/2);


    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        setVisible(false);
    }



}
