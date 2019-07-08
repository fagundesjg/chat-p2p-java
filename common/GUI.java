/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import javax.swing.JFrame;
import javax.swing.SwingConstants;

/**
 *
 * @author Dinho
 */
public abstract class GUI extends JFrame{
    public GUI(String title){
        super(title);
        initComponents();
        configComponents();
        insertComponents();
        insertActions();
        start();
    }
    protected abstract void initComponents();
    protected abstract void configComponents();
    protected abstract void insertComponents();
    protected abstract void insertActions();
    protected abstract void start();
}
