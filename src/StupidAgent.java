import java.util.Random;

public class StupidAgent extends Agent {
    private static int MaxMovesWithoutSuck = 100;

    // 1 - left, 2 - right.
    private int lastTurns[];

    private int turnsWithoutSucks;

    private  int[] Xrange;
    private  int[] Yrange;
    private int[] position = new int[2];
    private boolean[][] WasPositioned = new boolean[11][11];
    private int[] direction;

    private Action action;
    private boolean isDirt;
    private Random random;

    public StupidAgent() {
        super();
        random = new Random();
        lastTurns = new int[2];
        turnsWithoutSucks = 0;
        position[0] = 6;
        position[1] = 6;

        Xrange = new int[]{5,5};
        Yrange = new int[]{5,5};
        direction = new int[] {0,1};
        WasPositioned[6][6] = true;
    }
    @Override
    public void see(Percept p) {
        if (isDirt) {
            isDirt = false;
            action = new SuckDirt();
            return;
        }

        if (p instanceof VacPercept){
            turnsWithoutSucks += 1;
            VacPercept vp = (VacPercept) p;
            if (turnsWithoutSucks > MaxMovesWithoutSuck){
                action = new ShutOff();
                return;
            }
            if (vp.seeDirt()){
                action = new SuckDirt();
                turnsWithoutSucks = 0;
                return;
            }
            if (vp.seeDirtInFront()){
                isDirt = true;
                action = new GoForward();
                return;
            }
            if (vp.feelBump()) {
                action = selectSideToTurn();
                return;
            }
            if (vp.seeObstacle()){
                action = selectSideToTurn();
                return;
            }
            if (vp.seeDirt()){
                isDirt = true;
                // action = new GoForward();
                return;
            }
            action = new GoForward();

            position[0] += direction[0];
            position[1] += direction[1];

            // WasPositioned[position[0]][position[1]] = true;
        }
    }

    @Override
    public Action selectAction() {
        if (action != null)
            return action;

        int i;
        i = random.nextInt(5);
        switch (i){
            case 1:
                return new GoForward();
            case 2:
                return new TurnRight();
            case 3:
                return new TurnLeft();
            default:
                return new SuckDirt();
        }
    }

    @Override
    public String getId() {
        return "ABC";
    }

    private Action selectSideToTurn(){
        int countLeft = 0;
        int countRight = 0;
        int count = 0;

        for(int s : lastTurns)
            switch (s){
                case 1:
                    countLeft++;
                    count++;
                    break;
                case 2:
                    countRight++;
                    count++;
                    break;
                default:
                    count++;
                    break;
            }
        if ((countLeft / count) < 0.33)
            if ((lastTurns[lastTurns.length - 1] != 1) &&(lastTurns[lastTurns.length - 2] != 1))
                return turnLeft();
        if ((countRight / count) < 0.33)
            if ((lastTurns[lastTurns.length - 2] != 2) &&(lastTurns[lastTurns.length - 2] != 2))
                return turnRight();

        switch (lastTurns[lastTurns.length - 1]){
            case 1:
                return turnRight();
            case 2:
                return turnLeft();
            default:
                return turnRight();
        }
    }

    private Action turnLeft(){
        if (lastTurns.length == 1)
            lastTurns[0] = 1;
        if (lastTurns.length > 1) {
            for (int i = 1; i < lastTurns.length; i++)
                lastTurns[i - 1] = lastTurns[i];
            lastTurns[lastTurns.length - 1] = 1;
        }



        return new TurnLeft();
    }

    private Action turnRight(){
        if (lastTurns.length == 1)
            lastTurns[0] = 2;
        if (lastTurns.length > 1) {
            for (int i = 1; i < lastTurns.length; i++)
                lastTurns[i - 1] = lastTurns[i];
            lastTurns[lastTurns.length - 1] = 2;
        }
        return new TurnRight();
    }

}
