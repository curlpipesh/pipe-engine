package me.curlpipesh.engine.util;

/**
 * A simple 2-dimensional vector
 *
 * @author c
 * @since 5/21/15
 */
@SuppressWarnings("unused")
public class Vec2i {
    /**
     * x- and y- coordinates of the vector
     */
    private int x, y;
    
    /**
     * Creates a new vector with the supplied x- and y- coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public Vec2i(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Adds another vector to this one
     *
     * @param v The vector to add
     * @return Itself
     */
    public Vec2i add(final Vec2i v) {
        x += v.x;
        y += v.y;
        return this;
    }
    
    /**
     * Subtracts another vector from this one
     *
     * @param v The vector to subtract
     * @return Itself
     */
    public Vec2i sub(final Vec2i v) {
        x -= v.x;
        y -= v.y;
        return this;
    }
    
    /**
     * Multiplies this vector by another one
     *
     * @param v The vector to multiply by
     * @return Itself
     */
    public Vec2i mul(final Vec2i v) {
        x *= v.x;
        y *= v.y;
        return this;
    }
    
    /**
     * Divides this vector by another one
     *
     * @param v The vector to divide by
     * @return Itself
     */
    public Vec2i div(final Vec2i v) {
        x /= v.x;
        y /= v.y;
        return this;
    }
    
    /**
     * Returns the distance from this vector to the supplied vector
     *
     * @param v The vector to get the distance to
     * @return The distance from this vector to the supplied vector
     */
    public double dist(final Vec2i v) {
        return Math.sqrt(x*v.x+y*v.y);
    }
    
    /**
     * Returns the dot product of this vector with the supplied vector
     *
     * @param v The vector to do the dot product with
     * @return The dot product
     */
    public int dot(final Vec2i v) {
        return x*v.x + y*v.y;
    }
    
    /**
     * Returns the cross product of this vector with the supplied vector
     *
     * @param v The vector to do the cross product with
     * @return The cross product
     */
    public int cross(final Vec2i v) {
        return x*v.y - y*v.x;
    }
    
    /**
     * Adds the supplied amount to the x-coordinate
     *
     * @param x The amount to add
     * @return Itself
     */
    public Vec2i addX(final int x) {
        this.x += x;
        return this;
    }
    
    /**
     * Adds the supplied amount to the y-coordinate
     *
     * @param y The amount to add
     * @return Itself
     */
    public Vec2i addY(final int y) {
        this.y += y;
        return this;
    }
    
    /**
     * Sets the x- and y-coordinates of ths vector to those of the supplied
     * vector.
     *
     * @param v The vector to "clone"
     * @return Itself
     */
    public Vec2i set(final Vec2i v) {
        x = v.x;
        y = v.y;
        return this;
    }
    
    /**
     * The x-coordinate of this vector
     *
     * @return The x-coordinate
     */
    public int x() {
        return x;
    }
    
    /**
     * The y-coordinate of this vector
     *
     * @return The y-coordinate
     */
    public int y() {
        return y;
    }
    
    /**
     * Sets the x-coordinate of this vector
     *
     * @param x The new x-coordinate
     */
    public void x(final int x) {
        this.x = x;
    }
    
    /**
     * Sets the y-coordinate of this vector
     *
     * @param y The new y-coordinate
     */
    public void y(final int y) {
        this.y = y;
    }
    
    /**
     * Returns a String representation of this vector
     *
     * @return A String representation of this vector
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}