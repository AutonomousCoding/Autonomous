package AutonomousCoding;

//**********************************************************
// Script: Yellow Dye Maker
//
// User: Autonomous
//
// Author: AutonomousCoding
//
// Date: December 24, 2015
//
//Description: Gathers onions and turns them into Yellow
//  Dye in Draynor.
//
//*********************************************************

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.Random;

import org.powerbot.script.*;
import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Game.Crosshair;
import org.powerbot.script.rt6.Component;
import org.w3c.dom.css.Rect;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@Script.Manifest(name = "Autonomous Bank Organizer", description = "Organizes your bank")

public class bankOrganizer extends PollingScript<ClientContext> implements PaintListener{
    boolean sorted = false;
    boolean collapsed = false;
    boolean initialized = false;
    String memberItems = ctx.widgets.component(762, 76).text();
    String nonMemberItems = ctx.widgets.component(762, 72).text();
    int memberItemsTotal = Integer.parseInt(memberItems);
    int nonMemberItemsTotal = Integer.parseInt(nonMemberItems);
    int totalItems = memberItemsTotal + nonMemberItemsTotal;
    private long startTime = System.currentTimeMillis();


    String[] magicItemString = {"super", "potion", "unf", "antipoison", "vial", "dye", " rune", "talisman", "staff",
            "robe",
             " robe", "teleport", "grimy", "clean" , "weeds", "grass", "amulet", "necklace", "ring"};
    String[] foodSummoningItemString = {"pouch", "chard", "charm", "seed", "egg", " egg", "feather"};
    String[] combatItemString = {"coins", "gp", "chestplate", "tassets", "armour", "platebody", "platelegs",
            "initiate",
            "helm", "whip", "boots", "gloves", "gauntlets", "long", "god", "scimitar", "shield", "toktz", "maul", "two-hand",
            "off-hand", "spear", "bow", "bolts", "arrow", "cape", "hood"};
    String[] resourceItemString = {"harpoon", "rocktail", "fish", "shark", "trout", "salmon", "lobster", "pick", "ore",
            "bar",
            "rock", " rock", "gem", "uncut", "ruby", "emerald", "sapphire", "hatchet", "logs", "tinder", "plank", "nails",
            "bucket", "nest", " nest", "box"};


    /*
    tab2Input.setText("super, potion, unf, antipoison, vial, dye,  run, talisman, staff, robe, "
            + "teleport, grimy, clean, weeds, grass, amulet, necklace, ring");
    tab3Input.setText("pouch, chard, charm, seed, egg, feather");
    tab4Input.setText("coins, gp, chestplate, tassets, armour, platebody, platelegs, initiate, "
            + "helm, whip, boots, gloves, gauntlets, long, god, scimitar, shield, toktz, maul, two-hand, "
            + "off-hand, spear, bow, bolts, arrow, cape, hood");
    tab5Input.setText("harpoon, rocktail, fish, shark, trout, salmon, lobster, pick, ore, bar, rock, "
            + "gem, uncut, ruby, emerald, sapphire, hatchet, logs, tinder, plank, nails, bucket, nest, "
            + "box");
    */

    public void poll() {

        switch (state()) {
            case COLLAPSE: {
                if (collapsed == false) {

                    if (!ctx.widgets.component(762, 166).visible()) {
                        System.out.println("collapsed: true");
                        collapsed = true;
                        break;
                    }
                    //ctx.widgets.component(762, 158).click("Collapse tab 2", Crosshair.ACTION)
                    System.out.println("Found a tab");
                    ctx.widgets.component(762, 158).interact("Collapse tab 2");
                    Condition.sleep(1000);
                    if (ctx.widgets.component(762, 310).visible()) {
                        ctx.widgets.component(762, 310).click();
                        Condition.sleep(2000);
                    }
                }
                System.out.println("Finished loop.");
                break;

            }
            case ORGANIZING: {
                //Sort by grouping
                String[][] userStrings = new String[4][50];
                ArrayList<ArrayList<Component>> userBank;
                ArrayList<Component> bankItems;

                userStrings[0] = magicItemString;
                userStrings[1] = foodSummoningItemString;
                userStrings[2] = combatItemString;
                userStrings[3] = resourceItemString;

                bankItems = getUserBank();
                userBank = sortBank(userStrings, bankItems);

                System.out.println("Sorted Bank: ");
                for (int i = 0; i < userBank.size(); i++){
                    for (int j = 0; j < userBank.get(i).size(); j++){
                        System.out.println("userBank.get(" + i + ").get(" + j + "): " + userBank.get(i).get(j).itemName());
                    }
                }

                for (int i = 0; i < userBank.size(); i++){
                    for (int j = 0; j < userBank.get(i).size(); j++){
                        Rectangle vr = userBank.get(i).get(j).viewportRect();
                        ctx.widgets.scroll(userBank.get(i).get(j), ctx.widgets.component(
                                Constants.BANK_WIDGET, Constants.BANK_SCROLLBAR), vr.contains(ctx.input.getLocation()));

                        ctx.input.move(userBank.get(i).get(j).centerPoint());
                        System.out.println("userBank.get(" + i + ").get(" + j + "): " + userBank.get(i).get(j).itemName());
                        ctx.input.drag(ctx.widgets.component(762, (158+(i*8))).centerPoint(), true);
                        ctx.input.release(1);
                        userBank.get(i).remove(j);

                        Condition.sleep(1500);
                    }
                }


                /*
                for (int i = 0; i < numTabs; i++){
                    System.out.println("Currently moving items in tab " + i);
                    for (int j = 0; j < userBank.get(i).size(); j++){
                        System.out.println("Moving " + userBank.get(i).get(j).itemName() + " of " + userBank.get(i).size() +
                                " to" +
                                " Tab " + i);
                        //System.out.println("userBank[" + i + "][" + j + "]: " + userBank.get(i).get(j).itemName());
                        vr = userBank.get(i).get(j).viewportRect();

                        ctx.widgets.scroll(userBank.get(i).get(j), ctx.widgets.component(
                                Constants.BANK_WIDGET, Constants.BANK_SCROLLBAR), vr.contains(ctx.input.getLocation()));

                        ctx.input.move(userBank.get(i).get(j).centerPoint());
                        ctx.input.drag(ctx.widgets.component(762, (158+(i*8))).centerPoint(), true);
                        ctx.input.release(1);
                        Condition.sleep(1500);

                    }
                }
                */

                //MOST RECENT WORKING VERSION
                /*
                for (int j = 0; j < magicItemString.length; j++) {
                    for (int i = 0; i < totalItems; i++){
                        vr = ctx.widgets.component(762, 243).component(i).viewportRect();

                        ctx.widgets.scroll(ctx.widgets.component(762, 243).component(i), ctx.widgets.component(
                                Constants.BANK_WIDGET, Constants.BANK_SCROLLBAR), vr.contains(ctx.input.getLocation()));
                        String currentItem = ctx.widgets.component(762, 243).component(i).itemName().toString().toLowerCase();
                        System.out.println("Current item: " + currentItem);
                        if (!movedItems.contains(currentItem) && currentItem.contains(magicItemString[j])) {
                            System.out.println(currentItem + " is a " + magicItemString[j]);
                            System.out.println("Moving " + currentItem);
                            //item remove from list
                            ctx.input.move(ctx.widgets.component(762, 243).component(i).centerPoint());
                            ctx.input.drag(ctx.widgets.component(762, 158).centerPoint(), true);
                            ctx.input.release(1);
                            movedItems.add(currentItem.toString());
                            Condition.sleep(1500);
                        }
                    }
                }
                for (int j = 0; j < foodSummoningItemString.length; j++) {
                    for (int i = 0; i < totalItems; i++){
                        vr = ctx.widgets.component(762, 243).component(i).viewportRect();
                        ctx.widgets.scroll(ctx.widgets.component(762, 243).component(i), ctx.widgets.component(
                                Constants.BANK_WIDGET, Constants.BANK_SCROLLBAR), vr.contains(ctx.input.getLocation()));

                        String currentItem = ctx.widgets.component(762, 243).component(i).itemName().toString().toLowerCase();
                        System.out.println("Current item: " + currentItem);
                        if (!movedItems.contains(currentItem) && currentItem.contains(foodSummoningItemString[j])) {
                            System.out.println(currentItem + " is a " + foodSummoningItemString[j]);
                            System.out.println("Moving " + currentItem);
                            ctx.input.move(ctx.widgets.component(762, 243).component(i).centerPoint());
                            ctx.input.drag(ctx.widgets.component(762, 166).centerPoint(), true);
                            ctx.input.release(1);
                            movedItems.add(currentItem.toString());
                            Condition.sleep(1500);
                        }


                    }
                }
                for (int j = 0; j < combatItemString.length; j++) {
                    for (int i = 0; i < totalItems; i++){
                        Rectangle vr = ctx.widgets.component(762, 243).component(i).viewportRect();
                        ctx.widgets.scroll(ctx.widgets.component(762, 243).component(i), ctx.widgets.component(
                                Constants.BANK_WIDGET, Constants.BANK_SCROLLBAR), vr.contains(ctx.input.getLocation()));

                        String currentItem = ctx.widgets.component(762, 243).component(i).itemName().toString().toLowerCase();
                        System.out.println("Current item: " + currentItem);
                        if (!movedItems.contains(currentItem) && currentItem.contains(combatItemString[j])) {
                            System.out.println(currentItem + " is a " + combatItemString[j]);
                            System.out.println("Moving " + currentItem);
                            ctx.input.move(ctx.widgets.component(762, 243).component(i).centerPoint());
                            ctx.input.drag(ctx.widgets.component(762, 174).centerPoint(), true);
                            ctx.input.release(1);
                            movedItems.add(currentItem.toString());
                            Condition.sleep(1500);
                        }

                    }
                }

                for (int j = 0; j < resourceItemString.length; j++) {
                    for (int i = 0; i < totalItems; i++){
                        Rectangle vr = ctx.widgets.component(762, 243).component(i).viewportRect();
                        ctx.widgets.scroll(ctx.widgets.component(762, 243).component(i), ctx.widgets.component(
                                Constants.BANK_WIDGET, Constants.BANK_SCROLLBAR), vr.contains(ctx.input.getLocation()));

                        String currentItem = ctx.widgets.component(762, 243).component(i).itemName().toString().toLowerCase();
                        System.out.println("Current item: " + currentItem);
                        if (!movedItems.contains(currentItem) && currentItem.contains(resourceItemString[j])) {
                            System.out.println(currentItem + " is a " + resourceItemString[j]);
                            System.out.println("Moving " + currentItem);
                            ctx.input.move(ctx.widgets.component(762, 243).component(i).centerPoint());
                            ctx.input.drag(ctx.widgets.component(762, 182).centerPoint(), true);
                            ctx.input.release(1);
                            movedItems.add(currentItem.toString());
                            Condition.sleep(1500);
                        }
                    }
                }
                */

                sorted = true;
                break;
            }
            case OPENING_BANK: {
                ctx.bank.open();
                break;
            }
            case STOP: {
                ctx.controller.stop();
                break;
            }
            case INITIALIZING_TABS: {
                initialized = true;
                break;
            }
        }
    }

    private State state() {

        //System.out.println("Getting State");

        if (!ctx.bank.opened()){
            //System.out.println("OPENING_BANK");
            return State.OPENING_BANK;
        }
        else if (ctx.bank.opened() && collapsed == false){
            //System.out.println("COLLAPSING");
            return State.COLLAPSE;
        }
        else if (collapsed && !initialized){
            //System.out.println("INITIALIZING");
            return State.INITIALIZING_TABS;
        }
        else if (initialized && !sorted){
            //System.out.println("ORGANIZING");
            return State.ORGANIZING;
        }
        else {
           // System.out.println("STOPPING");
            return State.STOP;
        }
    }

    private enum State {

        ORGANIZING, STOP, COLLAPSE, OPENING_BANK, INITIALIZING_TABS

    }

    @Override
    public void repaint(Graphics g) {
        long minutes = (System.currentTimeMillis() - startTime)/1000/60;
        long seconds = (System.currentTimeMillis() - startTime)/1000%60;

        g.setColor(Color.darkGray);
        g.fillRect(0,314,280,75);
        g.setColor(Color.black);
        g.drawRect(0,314,280,75);
        g.drawString("Current State: " + state().toString(), 5, 330);
        g.drawString("Currently running for " + minutes + " minutes " + seconds + " seconds.", 5, 350);
        Font title = new Font("Times New Roman", Font.PLAIN, 16);
        g.setFont(title);
        g.drawString("Autonomous Bank Organizer", 5, 310);
    }

    private ArrayList<Component> getUserBank () {
        ArrayList<Component> bankItems = new ArrayList<>();
        Rectangle vr;

        for (int i = 0; i < totalItems; i++){
            vr = ctx.widgets.component(762, 243).component(i).viewportRect();
            ctx.widgets.scroll(ctx.widgets.component(762, 243).component(i), ctx.widgets.component(
                    Constants.BANK_WIDGET, Constants.BANK_SCROLLBAR), vr.contains(ctx.input.getLocation()));

            bankItems.add(ctx.widgets.component(762, 243).component(i));
        }

        return bankItems;
    }

    private ArrayList<ArrayList<Component>> sortBank (String[][] userStrings, ArrayList<Component>
            bankItems){
        ArrayList<ArrayList<Component>> bankCopy = new ArrayList<>();

        for (int i = 0; i < userStrings.length; i++){
            if (userStrings[i][0] != ""){
                bankCopy.add(new ArrayList<>());
            }
        }

        for (int i = 0; i < bankCopy.size(); i++){
            for (int j = 0; j < bankItems.size(); j++){
                for (int k = 0; k < userStrings[i].length; k++) {
                    if (bankItems.get(j).itemName().toLowerCase().contains(userStrings[i][k]) && !bankCopy
                            .get(i).contains(bankItems.get(j))) {
                        bankCopy.get(i).add(bankItems.get(j));
                        bankItems.remove(j);
                    }
                }
            }
        }

        return bankCopy;
    }

}
