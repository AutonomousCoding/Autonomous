import java.util.Random;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.*;

@Script.Manifest(name = "Dye Script", description = "Gathers onions and turns them into Yellow Dye in Draynor.", properties = "v1.0")

public class DyeMaker extends PollingScript<ClientContext> {
    private static final int ONION = 3366;
    private static final int DOOR = 1239;
    private static final int AGGIE = 922;
    private static final Random rand = new Random();
    private final Area BANK_AREA = new Area(
            new Tile(3092, 3240, 0),
            new Tile(3092, 3246, 0),
            new Tile(3096, 3246, 0),
            new Tile(3097, 3240, 0)
    );
    private final Area ONION_AREA = new Area(
            new Tile(3095, 3237, 0),
            new Tile(3101, 3237, 0),
            new Tile(3101, 3235, 0),
            new Tile(3095, 3235, 0)
    );
    private final Area AGGIE_HOUSE = new Area(
            new Tile(3081, 3258, 0),
            new Tile(3087, 3258, 0),
            new Tile(3087, 3262, 0),
            new Tile(3081, 3262, 0)
    );
    private final Tile[] TO_AGGIE = new Tile[] {
            new Tile(3103, 3235, 0),
            new Tile(3102, 3241, 0),
            new Tile(3101, 3243, 0),
            new Tile(3100, 3246, 0),
            new Tile(3097, 3249, 0),
            new Tile(3094, 3257, 0),
            new Tile(3091, 3258, 0),
            new Tile(3086, 3259, 0)
    };
    private final Tile[] TO_BANK = new Tile[] {
            new Tile(3085, 3259, 0),
            new Tile(3090, 3259, 0),
            new Tile(3093, 3258, 0),
            new Tile(3097, 3250, 0),
            new Tile(3093, 3243, 0)
    };
    private final TilePath aggiepath = new TilePath(ctx, TO_AGGIE);
    private final TilePath bankpath = new TilePath(ctx, TO_BANK);

    @Override

    public void poll() {

        final State state = state();

        switch (state) {
            case GATHERING_ONIONS: {
                final GameObject onion = ctx.objects.select().id(ONION).nearest().poll();
                if (ctx.backpack.count() <= 28){
                    if (ctx.players.local().animation() == -1){
                        if (onion.inViewport()){
                            ctx.camera.turnTo(onion);
                            onion.interact("Pick");
                            Condition.sleep(1500);
                        }
                        else{
                            ctx.movement.step(onion);
                            ctx.camera.turnTo(onion);
                            Condition.sleep(1500);
                        }
                    }
                }
                break;
            }



            case WALK_BANK: {
                if (ctx.objects.select(5).id(DOOR).nearest().poll().inViewport()){
                    System.out.println("There's a door.");
                    ctx.camera.turnTo(ctx.objects.select().id(DOOR).nearest().poll());
                    ctx.objects.select(5).id(DOOR).nearest().poll().interact("Open");
                    Condition.sleep(1500);
                    ctx.movement.step(BANK_AREA.getRandomTile());
                }
                else{
                    bankpath.traverse();
                }
                Condition.sleep(1500);
                break;

            }

            case WALK_AGGIE: {
                if (ctx.objects.select(5).id(DOOR).nearest().poll().inViewport()){
                    ctx.camera.turnTo(ctx.objects.select().id(DOOR).nearest().poll());
                    System.out.println("There's a door.");
                    ctx.objects.select(5).id(DOOR).nearest().poll().interact("Open");
                    Condition.sleep(1500);
                    ctx.movement.step(AGGIE_HOUSE.getRandomTile());
                }
                else{
                    aggiepath.traverse();
                }
                Condition.sleep(1500);
                break;
            }

            case WALK_ONIONS: {
                ctx.movement.step(ONION_AREA.getRandomTile());
                Condition.sleep(1500);
                break;

                /*
                if(!ctx.objects.select(15).id(GATE).isEmpty()){
                    ctx.objects.nearest().poll().interact("Open");
                }
                */
            }

            case BANK: {
                if(!ctx.bank.opened()){
                    ctx.bank.open();
                    Condition.sleep(1000);
                }

                else{
                    if(rand.nextInt(100) < 95){
                        ctx.bank.depositInventory();
                        Condition.sleep(1000);
                        if(rand.nextInt(100) > 95){
                            ctx.bank.close();
                            Condition.sleep(1000);
                        }
                    }
                    else{
                        ctx.bank.deposit(10000, 28);
                        Condition.sleep(1000);
                        if(rand.nextInt(100) > 85){
                            ctx.bank.close();
                            Condition.sleep(1000);
                        }
                    }
                }

                break;

            }

            case STOP: {
                System.out.println("You do not have enough money :(");
                ctx.controller.stop();
            }

            case MAKING_DYE: {
                ctx.camera.turnTo(ctx.objects.select().id(AGGIE).nearest().poll());
                ctx.npcs.select().id(AGGIE).nearest().poll().interact("Make-dyes");
                Condition.sleep(1500);
                ctx.input.send("3");
                Condition.sleep(1500);
                ctx.chat.clickContinue();
                if(rand.nextInt(10) < 3){
                    ctx.chat.clickContinue();
                }
                break;

            }
        }
    }




    private State state() {

        System.out.println("Getting State");

        if(ctx.backpack.select().id(1957).count() == 28 && !AGGIE_HOUSE.contains(ctx.players.local())){
            System.out.println("Walking to Aggie");
            return State.WALK_AGGIE;
        }
        if (!ONION_AREA.contains(ctx.players.local()) && ctx.backpack.select().id(1765).count() == 0
                && ctx.backpack.select().id(1957).count() == 0){
            System.out.println("Walking to Onions");
            return State.WALK_ONIONS;
        }
        if(AGGIE_HOUSE.contains(ctx.players.local()) && ctx.backpack.select().id(1957).count() > 1 && ctx.backpack.moneyPouchCount() >= 5){
            System.out.println("Making Dye");
            return State.MAKING_DYE;
        }
        if(BANK_AREA.contains(ctx.players.local()) && ctx.backpack.select().id(1765).count() == 14){
            System.out.println("Banking");
            return State.BANK;
        }
        if(ctx.backpack.select().id(1765).count() == 14){
            System.out.println("Walking to the Bank");
            return State.WALK_BANK;
        }
        if(ctx.backpack.moneyPouchCount() < 5){
            return State.STOP;
        }
        else {
            System.out.println("Gathering Onions");
            return State.GATHERING_ONIONS;
        }
    }



    private enum State {

        BANK, WALK_BANK, MAKING_DYE, GATHERING_ONIONS, WALK_ONIONS, WALK_AGGIE, STOP

    }

}
