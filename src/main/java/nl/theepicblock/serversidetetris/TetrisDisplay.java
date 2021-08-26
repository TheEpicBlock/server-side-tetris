package nl.theepicblock.serversidetetris;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3f;

import static nl.theepicblock.serversidetetris.TetrisState.HEIGHT;
import static nl.theepicblock.serversidetetris.TetrisState.WIDTH;

public class TetrisDisplay {
    private static final float PROXIMITY = 0.02f;
    private static final float SCALE     = 0.1f;
    private static final Vec3f BLACK = new Vec3f(0,0,0);
    private static final Vec3f WHITE = new Vec3f(1,1,1);
    private static final Vec3f RED = new Vec3f(1,0,0);

    public static void displayGame(ServerPlayerEntity player, TetrisState state) {
        var area = state.getArea();
        var currentTetronimoPoints = state.getCurrentTetromino().getPoints(state.getTetronimoRotation());
        var currentTetronimoPos = state.getTetrominoPos();
        var x = 0;
        var y = 0;

        for (byte b : area) {
            var colour = TetrisState.fromColourId(b);

            for (var point : currentTetronimoPoints) {
                if (currentTetronimoPos.x() + point.x() == x && currentTetronimoPos.y() + point.y() == y) {
                    colour = state.getTetrominoColour();
                    break;
                }
            }

            var colourComponents = new Vec3f(colour.getColorComponents()[0], colour.getColorComponents()[1], colour.getColorComponents()[2]);
            ParticleUtil.sendRelative(colourComponents, SCALE,
                    -x * PROXIMITY + WIDTH*PROXIMITY/2,
                    y * PROXIMITY - HEIGHT*PROXIMITY/2, 1, 0, 0, 0, 0, 6, player);

            x++;
            if (x >= WIDTH) {
                x = 0; y++;
            }
        }
    }

    public static void displayStartScreen(ServerPlayerEntity player) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                var colour = (x+y) % 2 == 0 ? BLACK : WHITE;
                if (x == 0 || x == WIDTH-1 || y == 0 || y == HEIGHT-1) colour = BLACK;

                if (x > (WIDTH/2)-3 && x < (WIDTH/2+3)) {
                    if (Math.abs(y-(HEIGHT+1)/2.0f+0.5) < ((WIDTH/2.+3)-x)/2.) {
                        colour = RED;
                    }
                }

                ParticleUtil.sendRelative(colour, SCALE,
                        -x * PROXIMITY + WIDTH*PROXIMITY/2,
                        y * PROXIMITY - HEIGHT*PROXIMITY/2, 1, 0, 0, 0, 0, 6, player);
            }
        }
    }
}
