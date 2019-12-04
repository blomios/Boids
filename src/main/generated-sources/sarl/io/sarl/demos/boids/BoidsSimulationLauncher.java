/**
 * $Id$
 * 
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 * 
 * Copyright (C) 2014-2019 the original authors or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sarl.demos.boids;

import io.sarl.demos.boids.BoidsSimulation;
import io.sarl.demos.boids.Population;
import io.sarl.demos.boids.WallParameters;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import java.awt.Color;
import org.arakhne.afc.math.geometry.d2.d.Vector2d;

/**
 * The main class configuring the various boids populations and launching the simulation
 * 
 * @author Nicolas Gaud
 */
@SarlSpecification("0.10")
@SarlElementType(10)
@SuppressWarnings("all")
public class BoidsSimulationLauncher {
  /**
   * @param args command line arguments
   */
  public static void main(final String... args) {
    BoidsSimulation simu = new BoidsSimulation();
    Population pRed = new Population(Color.RED);
    Population pGreen = new Population(Color.GREEN);
    Population pBlue = new Population(Color.BLUE);
    Vector2d _vector2d = new Vector2d(50, 0);
    Vector2d _vector2d_1 = new Vector2d(51, 200);
    WallParameters wall = new WallParameters(
      new Vector2d[] { _vector2d, _vector2d_1 });
    simu.addWall(wall);
    Vector2d _vector2d_2 = new Vector2d((-50), 0);
    Vector2d _vector2d_3 = new Vector2d((-101), (-200));
    WallParameters wall2 = new WallParameters(new Vector2d[] { _vector2d_2, _vector2d_3 });
    simu.addWall(wall2);
    Vector2d _vector2d_4 = new Vector2d(10, (-30));
    Vector2d _vector2d_5 = new Vector2d(15, (-130));
    Vector2d _vector2d_6 = new Vector2d(70, (-40));
    Vector2d _vector2d_7 = new Vector2d(105, (-130));
    WallParameters wall3 = new WallParameters(
      new Vector2d[] { _vector2d_4, _vector2d_5, _vector2d_6, _vector2d_7 });
    simu.addWall(wall3);
    for (int i = 0; (i < pRed.popSize); i++) {
      simu.addBoid(pRed);
    }
    for (int i = 0; (i < pGreen.popSize); i++) {
      simu.addBoid(pGreen);
    }
    for (int i = 0; (i < pBlue.popSize); i++) {
      simu.addBoid(pBlue);
    }
    simu.start();
  }
  
  @SyntheticMember
  public BoidsSimulationLauncher() {
    super();
  }
}
