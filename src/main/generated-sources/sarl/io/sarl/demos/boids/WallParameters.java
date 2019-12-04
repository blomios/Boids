package io.sarl.demos.boids;

import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import org.arakhne.afc.math.geometry.d2.d.Vector2d;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * @author Anthony Bouteiller
 */
@SarlSpecification("0.10")
@SarlElementType(10)
@SuppressWarnings("all")
public class WallParameters {
  @Accessors
  private Vector2d[] points;
  
  public WallParameters(final Vector2d[] p) {
    this.points = p;
  }
  
  @Override
  @Pure
  @SyntheticMember
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }
  
  @Override
  @Pure
  @SyntheticMember
  public int hashCode() {
    int result = super.hashCode();
    return result;
  }
  
  @Pure
  public Vector2d[] getPoints() {
    return this.points;
  }
  
  public void setPoints(final Vector2d[] points) {
    this.points = points;
  }
}
