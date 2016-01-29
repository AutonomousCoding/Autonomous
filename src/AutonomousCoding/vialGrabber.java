package AutonomousCoding;
//**********************************************************
// Script: Autonomous Vial Grabber
//
// User: Autonomous
//
// Author: AutonomousCoding
//
// Date: January 25, 2015
//
// Description: Grabs vials of water from wells.
//
//*********************************************************

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;

import javax.imageio.ImageIO;

@Script.Manifest(name = "Autonomous Vial Grabber", description = "Grabs vials of water from wells.")

public class vialGrabber extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    private long startTime = System.currentTimeMillis();
    private int vialsGrabbed = 0;

    long milliseconds;
    private int seconds;
    private int minutes;
    private int hours;
    private int vialCount;

    GameObject well = ctx.objects.select(10).id(89770).poll();
    GameObject well2 = ctx.objects.select(10).id(31044).poll();

    @Override

    public void poll() {

        final State state = state();

        switch (state) {
            case BANKING: {
                if (!ctx.bank.opened()){
                    ctx.bank.open();
                    Condition.sleep(1000);
                }
                if(ctx.bank.depositInventory()) {
                    ctx.bank.close();
                }
                Condition.sleep(1500);
                break;
            }
            case GRABBING: {
                System.out.println("Inside grabbing.");

                if (well.interact("Take Vials") || well2.interact("Take Vials") || well.interact(true, "Take Vials")
                        || well.interact(true, "Take Vials", "Portable Well")) {
                    System.out.println("Inside interact.");
                    Condition.sleep(1500);
                    ctx.input.send("28");
                    ctx.input.sendln("");
                    break;
                }
                else{
                    break;
                }
            }
            case STOP: {
                ctx.controller.stop();
            }
        }
    }

    private State state() {

        milliseconds = System.currentTimeMillis();
        seconds = ((int)milliseconds - (int)startTime)/1000%60;
        minutes = ((int)milliseconds - (int)startTime)/1000/60%60;
        hours = ((int)milliseconds - (int)startTime)/1000/60/60%60;

        vialCount = ctx.backpack.select().id(227).count();

        System.out.println("Getting State");


        if (!well.inViewport() && !well2.inViewport()){
            System.out.println("Stopping");
            return State.STOP;
        }else if (vialCount == 0){
            System.out.println("Grabbing");
            return State.GRABBING;
        }
        else {
            System.out.println("Banking.");
            return State.BANKING;
        }
    }

    private enum State {
        BANKING, GRABBING, STOP
    }

    @Override
    public void messaged(MessageEvent e) {

        final String msg = e.text().toLowerCase();

        if (e.source().isEmpty() && msg.contains("28 vials of water")) {
            vialsGrabbed+=28;
        }
    }

    @Override
    public void repaint(Graphics g) {

        try {
            URL url;

            BufferedImage img = null;

            Font font = new Font("Courier New", 1, 10);

            double hoursPercent = ((double)seconds/3600) + ((double)minutes/60) + (double)hours;

            int profitHr = 0;
            int grabbedHr = 0;
            int moneyMade = 0;

            String hour = String.format("%02d", hours);
            String min = String.format("%02d", minutes);
            String sec = String.format("%02d", seconds);
            String time = (hour + ":" + min + ":" + sec);

            moneyMade = vialsGrabbed*getPrice(227);
            profitHr = (int)(moneyMade/hoursPercent)/1000;
            url = new URL("http://i67.tinypic.com/vywrjl.png");
            grabbedHr = (int)(vialsGrabbed/hoursPercent);
            img = ImageIO.read(url);

            g.setFont(font);

            g.drawImage(img, 0, 314, 280, 75, null);
            g.drawString(time, 45, 346);
            g.drawString(Integer.toString(vialsGrabbed), 214, 345);
            g.drawString(Integer.toString(grabbedHr), 203, 362);
            g.drawString(state().toString(), 50, 377);

            if (profitHr > 999){
                profitHr = profitHr/1000;
                g.drawString(profitHr + "m", 51, 362);
            }
            else {
                g.drawString(profitHr + "k", 51, 362);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static int getPrice(int id) throws IOException {
        URL url = new URL("http://open.tip.it/json/ge_single_item?item=" + id);
        URLConnection con = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));

        String line = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            line += inputLine;
        }

        in.close(); // Remember to close it kids, cakemix loves memory.

        if (!line.contains("mark_price"))
            return -1;

        line = line.substring(line.indexOf("mark_price\":\"")
                + "mark_price\":\"".length());
        line = line.substring(0, line.indexOf("\""));

        line = line.replace(",", "");
        return Integer.parseInt(line);
    }

}
