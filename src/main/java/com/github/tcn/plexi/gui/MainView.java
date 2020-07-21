package com.github.tcn.plexi.gui;

import com.github.tcn.plexi.discordBot.PlexiBot;
import com.github.tcn.plexi.settingsManager.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

public class MainView extends JFrame {

    //create objects to be in the GUI
    private final JTextArea textArea;
    //This button will have different labels depending on current state of bot - "Start" or "Stop" - default to "Start"
    private final JButton buttonState = new JButton("Start");

    //get reference to settings obj
    Settings settings = Settings.getInstance();

    //get reference to plexi obj
    PlexiBot botInstance = PlexiBot.getInstance();

    public MainView() {
        //set title of window
        setTitle("Plexi " + settings.getVersionNumber());

        //create textarea - console output (not editable)
        textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        PrintStream guiOut = new PrintStream(new DualOutputStream(textArea));

        //set system output
        System.setErr(guiOut);
        System.setOut(guiOut);


        //Create GUI
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST; //I love how this uses cardinal directions instead of up/down/left/right

        //Add buttons
        add(buttonState, constraints);

        //add text box
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(new JScrollPane(textArea), constraints);

        //add event handler for start button
        buttonState.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                startStopButton();
            }
        });

        //set default action to happen when closing window
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //register WindowListener for the window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //we only need to prompt the user for conformation if plexi is currently running
                if (botInstance.isRunning()) {
                    int usrChoice = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit? This will stop plexi.");
                    if (usrChoice == 0) {
                        //we need to properly shut the bot down at this point
                        botInstance.stopBot();
                    } else {
                        //this means that the user decided to avoid shutdown. Print to log and return.
                        System.out.println("Avoided Shutdown");
                        return;
                    }
                }
                //shut down Jframe and exit program
                super.windowClosing(e);
                dispose();
                System.exit(0);
            }
        });

        //set window properties
        setSize(400, 335);
        setLocationRelativeTo(null);

    }

    private void startStopButton() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (botInstance.isRunning()) {
                    //ask the user if they really want to shutdown
                    int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to stop Plexi?");
                    if (choice == 0) {
                        botInstance.stopBot();
                        //now that the bot is off, change button label
                        if (!botInstance.isRunning()) {
                            buttonState.setText("Start");
                        } else {
                            System.out.println("Error: Unable to stop bot");
                        }
                    }
                } else {
                    botInstance.startBot();
                    //now that the bot is on, change button label
                    if (botInstance.isRunning()) {
                        buttonState.setText("Stop");
                    }
                }
            }
        });
        thread.start();
    }

    public void disableStart() {
        buttonState.setEnabled(false);
    }

}
