package mp3player;

// import com.sun.nio.sctp.SctpStandardSocketOptions;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ContextMenu extends JPopupMenu implements ActionListener
{
    MP3Player importedPlayer;
    JMenuItem addItem, removeItem, playItem;
    
    public ContextMenu(MP3Player importP)
    {
        importedPlayer = importP;
        addItem = new JMenuItem("Add");
        addItem.addActionListener(this);
        add(addItem);
        
        removeItem = new JMenuItem("Remove");
        removeItem.addActionListener(this);
        add(removeItem);
        
        playItem = new JMenuItem("Play");
        playItem.addActionListener(this);
        add(playItem);
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case "Add":
            {
                importedPlayer.initAdd();
                break;
            }
            case "Remove":
            {
                importedPlayer.initRemove();
                break;
            }
            case "Play":
            {
                importedPlayer.initPlay();
                break;
            }
        }
    }
}

