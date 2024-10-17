package io.jonuuh.camerautil;

public enum EaseType
{
    QUAD_IN(2),
    CUBIC_IN(3),
    QUART_IN(4),
    QUINT_IN(5),

    QUAD_OUT(2),
    CUBIC_OUT(3),
    QUART_OUT(4),
    QUINT_OUT(5),

    QUAD_IN_OUT(2),
    CUBIC_IN_OUT(3),
    QUART_IN_OUT(4),
    QUINT_IN_OUT(5);

    public final int scale;

    EaseType(int scale)
    {
        this.scale = scale;
    }
}
