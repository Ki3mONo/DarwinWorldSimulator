package agh.isc.oop.project.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EquatorForestMap extends AbstractWorldMap {

    public EquatorForestMap(Vector2d mapSize, int initialGrassCount) {
        super(mapSize);
        this.mapID = UUID.randomUUID();
        this.leftBottomCorner = new Vector2d(0, 0);
        this.rightUpperCorner = new Vector2d(mapSize.getX() - 1, mapSize.getY() - 1);
        initializeGrass(initialGrassCount);
    }

    //Czemu to zapisywanie do worldElements i po co w ogóle taka hashmapa? przydaje sie w kilku miejscach a w kilku bywa strasznie uciążliwa, nigdy wiecej XD
    @Override
    public void initializeGrass(int initialGrassSize) {
        Random random = new Random();
        int equatorStart = 2 * mapSize.getY() / 5;
        int equatorEnd = 3 * mapSize.getY() / 5 + 1;

        int equatorGrass = (int) (initialGrassSize * 0.8);
        int otherGrass = initialGrassSize - equatorGrass;

        for (int i = 0; i < equatorGrass; i++) {
            int x = random.nextInt(mapSize.getX());
            int y = equatorStart + random.nextInt(equatorEnd - equatorStart);
            Vector2d position = new Vector2d(x, y);
            super.putGrass(position);
        }

        for (int i = 0; i < otherGrass; i++) {
            int x = random.nextInt(mapSize.getX());
            int y;
            do {
                y = random.nextInt(mapSize.getY());
            } while (y >= equatorStart && y < equatorEnd);

            Vector2d position = new Vector2d(x, y);
            super.putGrass(position);
        }
    }
    @Override
    public void grassGrow(int dailyGrowth){
        Random random = new Random();
        int equatorStart = 2 * mapSize.getY() / 5;
        int equatorEnd = 3 * mapSize.getY() / 5 + 1;

        int equatorGrowth = (int) (dailyGrowth * 0.8);
        int otherGrowth = dailyGrowth - equatorGrowth;

        for (int i = 0; i < equatorGrowth; i++) {
            int x = random.nextInt(mapSize.getX());
            int y = equatorStart + random.nextInt(equatorEnd - equatorStart);
            Vector2d position = new Vector2d(x, y);
            super.putGrass(position);
        }

        for (int i = 0; i < otherGrowth; i++) {
            int x = random.nextInt(mapSize.getX());
            int y;
            do {
                y = random.nextInt(mapSize.getY());
            } while (y >= equatorStart && y < equatorEnd);

            Vector2d position = new Vector2d(x, y);
            super.putGrass(position);
        }
    }

}