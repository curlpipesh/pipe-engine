package me.curlpipesh.engine.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.curlpipesh.engine.Engine.EngineState;
import me.curlpipesh.engine.logging.LoggerFactory;
import me.curlpipesh.engine.util.Vec2d;
import me.curlpipesh.gl.tessellation.impl.VAOTessellator;
import me.curlpipesh.gl.vbo.Vbo;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * TODO: RenderServerPool? qq
 *
 * @author audrey
 * @since 11/17/15.
 */
@SuppressWarnings("unused")
public class RenderServer {
    @Getter
    private final LinkedList<RenderRequest> requests;

    private final VAOTessellator tess = new VAOTessellator(0xFFFF);
    private final List<Mesh> meshes = new ArrayList<>();

    private long lastUpdate = 0;

    @Getter
    @Setter
    private int maxNewRenders = 4;

    private final Logger logger;

    @SuppressWarnings("FieldCanBeLocal")
    private final EngineState state;

    @SuppressWarnings("FieldCanBeLocal")
    private final long id;

    public RenderServer(final EngineState state) {
        this.state = state;
        id = UUID.randomUUID().getMostSignificantBits() & 0xFFFFFFFFL;
        requests = new LinkedList<>();
        logger = LoggerFactory.getLogger(state, "Render server " + Long.toHexString(id));
    }

    public boolean request(final RenderRequest request) {
        if(!request.isCompiled()) {
            logger.warning("Render request '" + request.getName() + "' was not compiled, ignoring");
            return false;
        }
        if(request.getType() == RenderType.VBO) {
            logger.info("Queuing mesh render '" + request.getName() + "'");
            requests.addLast(request);
        } else if(request.getType() == RenderType.VAO) {
            logger.info("Handling immediate request '" + request.getName() + "'");
            tess.startDrawing(request.getMode());
            request.getVertices().stream().forEach(v -> tess.color(v.getColor())
                    .addVertex(v.getX(), v.getY(), v.getZ()));
            tess.bindAndDraw();
        }
        return true;
    }

    public void update() {
        final long now = System.nanoTime();
        if(TimeUnit.NANOSECONDS.toMillis(now - lastUpdate) > 100) {
            lastUpdate = now;
            if(!requests.isEmpty()) {
                if(requests.size() > maxNewRenders * maxNewRenders) {
                    ++maxNewRenders;
                }
                for(int i = 0; i < Math.min(maxNewRenders, requests.size()); i++) {
                    final RenderRequest request = requests.poll();
                    if(request != null) {
                        if(request.getType() == RenderType.VBO) {
                            logger.info("Rendering mesh '" + request.getName() + "'");
                            final Vbo vbo = new Vbo(request.getMode());
                            for(final Vertex v : request.getVertices()) {
                                vbo.color(v.getColor()).vertex(v.getX(), v.getY(), v.getZ());
                            }
                            vbo.compile();
                            final Mesh mesh = new Mesh(request.getName(), vbo, request.getPosition(), request.getDimensions());
                            if(meshes.stream().filter(m -> m.getName().equals(request.getName())).count() > 0) {
                                // Delete old VBO
                                final List<Mesh> deletes = new ArrayList<>();
                                meshes.stream().filter(m -> m.getName().equals(request.getName())).forEach(m -> {
                                    GL15.glDeleteBuffers(m.getVbo().getId());
                                    deletes.add(m);
                                });
                                meshes.removeAll(deletes);
                            }
                            meshes.add(mesh);
                        } else {
                            logger.warning("Was asked to render '" + request.getName() + "'[" + request.getType() + "], but it isn't a VBO!");
                            --i;
                        }
                    } else {
                        logger.severe("Got a null render request!?");
                        --i;
                    }
                }
            }
        }
    }

    public void render(final Vec2d renderOffset) {
        // For some reason these need to be rendered backwards. idfk.
        // Example is chunk mesh renders
        // Requesting renders as in the order of [chunk, debug] seems to be
        // rendering as [debug, chunk]

        //for(final Mesh mesh : meshes) {
        for(int i = meshes.size() - 1; i >= 0; i--) {
            // TODO: Check for off-screen
            final Mesh mesh = meshes.get(i);
            final double xPos = mesh.getPosition().x() - renderOffset.x();
            final double yPos = mesh.getPosition().y() - renderOffset.y();

            if((xPos < Display.getWidth() && yPos < Display.getHeight())
                    || (xPos + mesh.getDimensions().x() > 0 && yPos + mesh.getDimensions().y() > 0)) {
                GL11.glTranslated(xPos, yPos, 0);
                mesh.getVbo().render();
                GL11.glTranslated(-xPos, -yPos, 0);
            }
        }
    }

    private class Mesh {
        @Getter(AccessLevel.PRIVATE)
        private final String name;
        @Getter(AccessLevel.PRIVATE)
        private final Vbo vbo;
        @Getter(AccessLevel.PRIVATE)
        private final Vec2d position;
        @Getter(AccessLevel.PRIVATE)
        private final Vec2d dimensions;

        Mesh(final String name, final Vbo vbo, final Vec2d position, final Vec2d dimensions) {
            this.name = name;
            this.vbo = vbo;
            this.position = position;
            this.dimensions = dimensions;
        }
    }
}
