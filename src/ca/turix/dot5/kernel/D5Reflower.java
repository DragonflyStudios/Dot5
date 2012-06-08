package ca.turix.dot5.kernel;

import android.graphics.Point;

public class D5Reflower {
    public static final int DEFAULT_MARGIN = 24;
    public static final int DEFAULT_SPACE_WIDTH = 8;
    public static final int DEFAULT_LINE_SPACE = 24;

    public D5Reflower(int screenWidth, int screenHeight, int lineHeight, int spaceWidth)
    {
        m_screenWidth = screenWidth;
        m_screenHeight = screenHeight;
        m_lineHeight = lineHeight;        
    }
    
    public Point[] reflow(int[] boxWidths)
    {
        int size = boxWidths.length;
        m_coordinates = new Point[size];
        
        for (int i = 0; i < size; i++)
            addBox(i, boxWidths[i]);
        
        return m_coordinates;
    }
    
    private void addBox(int index, int boxWidth)
    {
        if (index == 0) {
            m_currentLineY = m_screenHeight / 2 - 2 * m_lineHeight;
            startNewLine(index, boxWidth);
        } else if (overflows(boxWidth)) {
            bumpUpOldLines(index);
            m_currentLineY += (m_lineHeight + DEFAULT_LINE_SPACE) / 2;
            startNewLine(index, boxWidth);
        } else
            extendCurrentLine(index, boxWidth);
    }
    
    private boolean overflows(int boxWidth)
    {
        return (m_currentLineEnd + DEFAULT_SPACE_WIDTH + boxWidth + DEFAULT_MARGIN > m_screenWidth);
    }
    
    private void bumpUpOldLines(int tailIndex)
    {
        // bumping by half a line for spreading both up and down
        for (int i = 0; i < tailIndex; i++)
            m_coordinates[i].y -= (m_lineHeight + DEFAULT_LINE_SPACE) / 2;
    }
    
    private void startNewLine(int index, int boxWidth)
    {
        m_currentLineEnd = (m_screenWidth + boxWidth) / 2;
        int currentLineStartX = (m_screenWidth - boxWidth) / 2;
        m_currentLineFirstWordIndex = index;
        m_coordinates[index] = new Point(currentLineStartX, m_currentLineY);        
    }
    
    private void extendCurrentLine(int index, int boxWidth)
    {
        int half = (boxWidth + DEFAULT_SPACE_WIDTH) / 2;
        for (int i = m_currentLineFirstWordIndex; i < index; i++)
            m_coordinates[i].x -= half;
        m_coordinates[index] = new Point(m_currentLineEnd - half + DEFAULT_SPACE_WIDTH, m_currentLineY);
        m_currentLineEnd += half;
    }
    
    public int getScreenWidth()    { return m_screenWidth; }    
    public void setScreenWidth(int screenWidth)    { m_screenWidth = screenWidth; }
    
    public int getScreenHeight() { return m_screenHeight; }
    public void setScreenHeight(int screenHeight) { m_screenHeight = screenHeight; }
        
    public int getLineHeight() { return m_lineHeight; }
    public void setLineHeight(int lineHeight) { m_lineHeight = lineHeight; }
    
    private int m_screenWidth;
    private int m_screenHeight;
    private int m_lineHeight;
    private Point[] m_coordinates;
    private int m_currentLineEnd;
    private int m_currentLineY;
    private int m_currentLineFirstWordIndex;
}
