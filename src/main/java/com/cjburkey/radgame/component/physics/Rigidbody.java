package com.cjburkey.radgame.component.physics;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.ecs.Component;
import java.util.UUID;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;
import org.joml.Vector2fc;

/**
 * Created by CJ Burkey on 2019/03/17
 */
@SuppressWarnings("WeakerAccess")
public class Rigidbody extends Component {

    private final Body body = new Body();

    public void onLoad() {
        RadGame.INSTANCE.physicsWorld.addBody(body);
    }

    public void onRemove() {
        RadGame.INSTANCE.physicsWorld.removeBody(body);
    }

    @Override
    public void onUpdate() {
        transform().position.set((float) body.getTransform().getTranslationX(), (float) body.getTransform().getTranslationY(), transform().position.z);
        transform().rotation.rotationZ((float) body.getTransform().getRotation());
    }

    public void removeCollider(final BodyFixture shape) {
        body.removeFixture(shape);
    }

    public void setGravity(double gravity) {
        body.setGravityScale(gravity);
    }

    public double getGravity() {
        return body.getGravityScale();
    }

    public void applyForce(final Vector2fc force) {
        body.applyForce(new Vector2(force.x(), force.y()));
    }

    public void applyForceAt(final Vector2fc force, final Vector2fc at) {
        body.applyForce(new Vector2(force.x(), force.y()), new Vector2(at.x(), at.y()));
    }

    public BodyFixture updateCollider(final Polygon shape) {
        body.removeAllFixtures();
        return addCollider(shape);
    }

    public BodyFixture addCollider(final Polygon shape) {
        return body.addFixture(shape);
    }

    public UUID getId() {
        return body.getId();
    }

}
