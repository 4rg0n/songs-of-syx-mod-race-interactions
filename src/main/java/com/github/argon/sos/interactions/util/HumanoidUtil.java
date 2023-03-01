package com.github.argon.sos.interactions.util;

import init.race.Race;
import settlement.entity.ENTITY;
import settlement.entity.humanoid.Humanoid;
import settlement.main.SETT;
import settlement.path.components.SComponent;
import settlement.stats.STATS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HumanoidUtil {
    public static List<Humanoid> getNearbyHumanoids(Humanoid humanoid, int radius) {
        SComponent settlementComponent = SETT.PATH().finders().otherHumanoid.findComp(humanoid, radius);
        if (settlementComponent == null) {
            return Collections.emptyList();
        }

        int dim = settlementComponent.level().size() + 2;
        int x1 = (settlementComponent.centreX()& -settlementComponent.level().size()) - 1;
        int y1 = (settlementComponent.centreY()& -settlementComponent.level().size()) - 1;
        int x2 = x1+dim;
        int y2 = y1+dim;
        int rx = x1;
        int ry = y1;

        List<Humanoid> likedHumanoids = new ArrayList<>();
        for (int y = 0; y < dim; y++) {
            for (int x = 0; x < dim; x++) {

                for (ENTITY entity : SETT.ENTITIES().getAtTile(rx, ry)) {
                    if (entity != humanoid && entity instanceof Humanoid) {
                        likedHumanoids.add((Humanoid) entity);
                    }
                }
                rx++;
                if (rx >= x2) {
                    rx = x1;
                    ry++;
                    if (ry >= y2) {
                        ry = y1;
                    }
                }
            }
        }

        return likedHumanoids;
    }

    /**
     * @return 0 or 1; currently a humanoid can only have one friend
     */
    public static int countFriends(Humanoid humanoid, List<Humanoid> nearbyHumanoids) {
        ENTITY entity = STATS.POP().FRIEND.get(humanoid.indu());

        if (entity != humanoid && entity instanceof Humanoid) {
            Humanoid friend = (Humanoid) entity;

            return (int) nearbyHumanoids.stream().filter(nearbyHumanoid ->
                nearbyHumanoid.equals(friend)
            ).count();
        }

        return 0;
    }

    public static double avgRaceLikings(Humanoid humanoid, List<Humanoid> nearbyHumanoids, boolean ownRace) {
        double likeScore = 0d;
        Race race = humanoid.race();

        for (Humanoid nearbyHumanoid : nearbyHumanoids) {
            Race otherRace = nearbyHumanoid.race();

            // skip own race?
            if (!ownRace && race.key.equals(otherRace.key)) {
                continue;
            }

            double liking = race.pref().other(otherRace);
            likeScore += liking;
        }

        if (likeScore == 0d) {
            return likeScore;
        }

        return nearbyHumanoids.size() / likeScore;
    }
}
