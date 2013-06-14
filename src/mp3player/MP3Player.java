package mp3player;

/**
 * @author Yao, Jia Huang & Pham, Khoa
 */

// import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.AuthorizeCallback;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javazoom.jl.player.Player;

public class MP3Player extends JFrame
{
    private MP3File mp3File;
    
    //Main UI
    private PlayerThread playt = new PlayerThread();
    private TimeKeepThread timet;
    private Thread runpt, timept;// = new Thread(playt);
    private boolean isPaused, firstPress = true, isPlaying = false;
    private JLabel songNameL, artistL, albumL, artL;
    private static JLabel timeL;
    private JButton playB, pauseB, stopB, nextB, prevB ,exitB;
    
    private listHandler lH;
    private buttonHandler bH;
    private mouseListHandler mLL;
    
    //Media Library UI
    private JButton addB, removeB;
    private JList songList;
    private JScrollPane listPane;
    private JFileChooser browse;
    private ArrayList<MP3File> mp3List;
    private ArrayList<String> strAr;
    private static int selIndex;
    
    private static int Position, savedPosition = 0;
    private long duration;
    private static String durationOut;
    private int pausePosition, startBytes;
    private static boolean stopFlag = false;
    private Map properties;
    
    //Song Properties
    private String title, author, album;
    
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int ALBUM_WIDTH = 150;
    private static final int ALBUM_HEIGHT = 150;
    
    private Container pane;
    public Player player;
    private InputStream iStream;
    
    public MP3Player()
    {
        //Creating Labels, Buttons, and Handler
        artL = new JLabel();
        //artL.setMaximumSize(new Dimension(200, 200));
        //artL.setBounds(40, 10, 50, 50);
        //artL.setLocation(0, 400);
        
        songNameL = new JLabel("//Song Name");
        songNameL.setFont(new Font("Arial", Font.BOLD, 35));
        songNameL.setForeground(Color.darkGray);
        artistL = new JLabel("//Artist");
        artistL.setFont(new Font("Arial", Font.BOLD, 20));
        artistL.setForeground(Color.gray);
        albumL = new JLabel("//Album");
        albumL.setFont(new Font("Arial", Font.BOLD, 20));
        albumL.setForeground(Color.gray);
        timeL = new JLabel("0:0 / 0:00");
        timeL.setFont(new Font("Arial", Font.BOLD, 15));
        timeL.setForeground(Color.gray);
        playB = new JButton("Play");
            playB.setForeground(Color.BLUE);
            playB.setToolTipText("Press to play song.");
        pauseB = new JButton("Pause");
            pauseB.setToolTipText("Press to pause song.");
        stopB = new JButton("Stop");
            stopB.setToolTipText(("Press to stop song."));
        nextB = new JButton("Next");
            nextB.setToolTipText("Press to play next song.");
        prevB = new JButton("Prev");
            prevB.setToolTipText("Press to play previous song.");
        exitB = new JButton("Exit");
            exitB.setToolTipText("Press to exit player.");
            exitB.setForeground(Color.red);
        bH = new buttonHandler();
            //Creating media library objects
            mp3List = new ArrayList<>();
            addB = new JButton("Add");
                addB.setToolTipText("Press to browse files.");
            removeB = new JButton("Remove");
                removeB.setToolTipText("Press to remove currently selected file from list.");
            songList = new JList();
            lH = new listHandler();
            mLL = new mouseListHandler();
            mLL.setMP3Player(this);
            songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            songList.addListSelectionListener(lH);
            songList.addMouseListener(mLL);
            songList.setVisibleRowCount(20);
            listPane = new JScrollPane(songList);
            listPane.setMinimumSize(new Dimension(700, 323));
            browse = new JFileChooser();
            browse.setMultiSelectionEnabled(true);
        
        //Connecting Buttons to Handler
        playB.addActionListener(bH);
        pauseB.addActionListener(bH);
        stopB.addActionListener(bH);
        prevB.addActionListener(bH);
        nextB.addActionListener(bH);
        exitB.addActionListener(bH);
        addB.addActionListener(bH);
        removeB.addActionListener(bH);
        
        //Add objects to container
        pane = getContentPane();
        GridBagLayout gbL = new GridBagLayout();
        pane.setLayout(gbL);
        GridBagConstraints gc = new GridBagConstraints();
        
        gc.weightx = 0.8;
        gc.weighty = 0.0;
        
        //First Column
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(5, 10, 0, 2); //top left bottom right
        gc.gridwidth = 1; //CHANGED FOR ALBUM ART!
        gc.gridx = 0;
        gc.gridy = 0;
        pane.add(songNameL, gc);
        gc.insets = new Insets(0, 10, 0, 2);
        gc.gridy = 2;
        pane.add(artistL, gc);
        gc.gridy = 3;
        pane.add(albumL, gc);
        gc.gridy = 4;
        pane.add(timeL, gc);
        
        gc.weightx = 0.0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.fill = GridBagConstraints.BOTH;
        //gc.insets = new Insets(50, 50, 2, 2);
        gc.gridheight = 5;
        gc.gridx = 1;
        gc.gridy = 0;
        pane.add(artL, gc);
        gc.gridheight = 1;
        
        //Second Column
        gc.anchor = GridBagConstraints.LINE_END;
        gc.weighty = 0.9;
        gc.gridwidth = 2;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 2;
        gc.gridy = 0;
        gc.insets = new Insets(5, 2, 2, 2); //top left bottom right
        pane.add(playB, gc);
        gc.insets = new Insets(2, 2, 2, 2);
        gc.gridy = 1;
        pane.add(pauseB, gc);
        gc.gridy = 2;
        pane.add(stopB, gc);
        
        //Prev/Next Buttons
        gc.insets = new Insets(5, 2, 2, 2);
        gc.gridwidth = 1;
        gc.gridy = 3;
        gc.ipadx = 17;
        pane.add(prevB, gc);
        gc.ipadx = 0;
        gc.gridx = 3;
        pane.add(nextB, gc);
        
        //gc.insets = new Insets(5, 0, 0, 0);
        gc.gridwidth = 2;
        gc.gridx = 2;
        gc.gridy = 4;
        pane.add(exitB, gc);
        gc.gridwidth = 1;
        
        //Media Library
        gc.ipady = 70;
        gc.weighty = 0.1;
        gc.gridx = 0;
        gc.gridy = 5;
        gc.gridwidth = 4;
        gc.gridheight = 1;
        pane.add(listPane, gc);
        
        //Add/Remove buttons
        gc.insets = new Insets(2, 2, 5, 2); //top left bottom right
        gc.ipady = 0;
        gc.gridwidth = 3;
        gc.gridheight = 1;
        gc.weighty = 0.5;
        //gc.weightx = 1.0;
        gc.gridx = 0;
        gc.gridy = 6;
        pane.add(addB, gc);
        //gc.weightx = 0.0;
        gc.gridwidth = 1;
        gc.ipadx = 0;
        gc.gridx = 3;
        gc.gridy = 6;
        pane.add(removeB, gc);
        
        setSize(WIDTH, HEIGHT);
        setTitle("MP3Player");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public MP3Player(String fileName)
    {
        this();
        createStream(mp3File.mp3.getPath());
    }
    
    public void createStream(String fileName)
    {
        try
        {
            iStream = new FileInputStream(fileName);
            
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startPlay()
    {
        playt = new PlayerThread();
        runpt = new Thread(playt);
        timet = new TimeKeepThread();
        timept = new Thread(timet);
        try
        {
            player = new Player(iStream);
        }
        catch(Exception e2)
        {
            e2.printStackTrace();
        }
        playt.setPlayer(player);
        timet.setPlayer(player);
        
        AudioFileFormat baseFileFormat = null;
        try 
            {
                baseFileFormat = AudioSystem.getAudioFileFormat(mp3File.mp3);
            } 
            catch (UnsupportedAudioFileException ex) 
            {
                Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        properties = baseFileFormat.properties();
        duration = (Long) properties.get("duration");
        durationOut = convertToDuration(duration);
        //System.out.println(durationOut);
    }
    
    //Threads section
    //----------------
        //Contains: PlayerThread, and TimeKeepThread
    private static class PlayerThread implements Runnable
    {
        Player player;
        String status = "Stop";
        int pauseTime = 0;
        public void setPlayer(Player pl)
        {
            player = pl;
        }
        public void setStatus(String s)
        {
            status = s;
        }
        public void setPausedTime(int pTime)
        {
            pauseTime = pTime;
        }
        @Override
        public void run()
        {
            try
            {
                if(pauseTime != 0)
                    player.play(pauseTime);
                else
                    player.play();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private static class TimeKeepThread implements Runnable
    {
        Player player;
        public void setPlayer(Player pl)
        {
            player = pl;
        }
        @Override
        public void run()
        {
            //Timer
            while(!player.isComplete() && !stopFlag)
            {
                if(Position == 0)
                    Position = player.getPosition();
                else
                    Position = savedPosition + player.getPosition();
                System.out.println("" + Position);
                timeL.setText("" + convertToDuration(Position) + " / " + durationOut);
                
                try
                {
                    Thread.sleep(900);
                }
                catch(Exception ee)
                {
                    ee.printStackTrace();
                }
            }
        }
    }
    //----------------------
    //End of Threads section
    
    //Handlers section
    //----------------
        //Contains: buttonHandler, listHandler, and mouseListHandler
    private class buttonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            switch(e.getActionCommand())
            {
                case "Exit":
                {
                    System.exit(0);
                    break;
                }
                case "Play":
                {
                    if("Pause".equals(playt.status)) //initPause again to resume,
                        initPause();                 //  instead of playing from beginning.
                    else
                        initPlay();
                    break;
                }
                case "Pause":
                {
                    if("Play".equals(playt.status))
                        initPause();
                    break;
                }
                case "Next":
                {
                    if(!mp3List.isEmpty())
                    {
                        if((mp3List.size() - 1 > selIndex))
                        {
                            if("Play".equals(playt.status))
                            initStop();
                            stopFlag = false;
                            mp3File = mp3List.get(selIndex + 1);
                            selIndex++;
                            songList.setSelectedIndex(selIndex);
                            playMethod(mp3File);
                            System.out.println("selIndex: " + selIndex);
                        }
                    }
                    break;
                }
                case "Prev":
                {
                    if(selIndex != 0)
                        {
                            if("Play".equals(playt.status))
                            initStop();
                            stopFlag = false;
                            mp3File = mp3List.get(selIndex - 1);
                            selIndex--;
                            songList.setSelectedIndex(selIndex);
                            playMethod(mp3File);
                            System.out.println("selIndex: " + selIndex);
                        }
                    break;
                }
                case "Stop":
                {
                    if("Play".equals(playt.status))
                        initStop();
                    break;
                }
                case "Add":
                {
                    initAdd();
                    break;
                }
                case "Remove":
                {
                    initRemove();
                    break;
                }
            }
        }
    }
    
    private class listHandler implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            selIndex = songList.getSelectedIndex();
            ImageIcon artIcon = null;
            if(!"Play".equals(playt.status) && (mp3List.size() > 0)) //Checks if song is already playing.
            {
                songNameL.setText(mp3List.get(selIndex).getTitle(properties, iStream));
                artistL.setText(mp3List.get(selIndex).getAuthor(properties, iStream));
                albumL.setText(mp3List.get(selIndex).getAlbum(properties, iStream));
                timeL.setText("0:0 / " + convertToDuration(mp3List.get(selIndex).getDuration(properties, iStream)));
                
                if(mp3List.get(selIndex).getArtwork() != null) //Checks if there is artwork first.
                {                                              //If no artwork, skip.
                    artIcon = new ImageIcon(mp3List.get(selIndex).getArtwork());
                    artL.setIcon(resizeImageIcon(artIcon));
                }
                else
                    artL.setIcon(new ImageIcon());
            }
        }
    }
    
    private class mouseListHandler implements MouseListener
    {
        MP3Player importedPlayer;
        public void setMP3Player(MP3Player importP)
        {
            importedPlayer = importP;
        }
        
        @Override
        public void mouseClicked(MouseEvent e)
        {
            JList list = (JList)e.getSource();
            
            if(e.getClickCount() == 2 && (mp3List.size() > 0))
            {
                if("Play".equals(playt.status))
                    initStop();
                int index = list.locationToIndex(e.getPoint());
                mp3File = mp3List.get(index);
                
                if(!mp3List.isEmpty())
                    {
                        if("Play".equals(playt.status))
                            initStop();
                        stopFlag = false;
                        playMethod(mp3File);
                    }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            doPop(e);
        }
        @Override
        public void mouseReleased(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            doPop(e);

        }
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        
        public void doPop(MouseEvent e)
        {
            ContextMenu menu = new ContextMenu(importedPlayer);
            menu.show(e.getComponent(), e.getX(), e.getY());
            
        }
    }
    //-----------------------
    //End of Handlers section
    
    //GUI song manipulation methods
    //-----------------------------
        //Contains: playMethod, initPause(), initStop(), initAdd(), initRemove(), and initPlay().
    private void playMethod(MP3File mp3F)
    {
        
        createStream(mp3F.mp3.getPath());
        try
        {
            startBytes = iStream.available();
            System.out.println("Bytes startBytes: " + startBytes);
        } 
        catch (IOException ex)
        {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        startPlay();
        playt.setStatus("Play");

        runpt.start();
        timept.start();

        isPaused = false;
        firstPress = false;

        //Get properties
        title = mp3File.getTitle(properties, iStream);
        author = mp3File.getAuthor(properties, iStream);
        album = mp3File.getAlbum(properties, iStream);
        songNameL.setText(title);
        artistL.setText(author);
        albumL.setText(album);
    }
    
    private void initPause()
    {
        int restartBytes = 0;
        savedPosition = Position;
        if(isPaused == false)
        {
            stopFlag = true;
            try 
            {
                pausePosition = iStream.available();
                System.out.println("Bytes pausePosition: " + pausePosition); 
            }
            catch (IOException ex)
            {
                Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(isPaused == true)
        {
            stopFlag = false;
            restartBytes = startBytes - pausePosition;
        }

        player.close();
        if(isPaused == true)
            isPaused = false;
        else if(isPaused == false)
            isPaused = true;

        if(isPaused == false)
        {
            createStream(mp3File.mp3.getPath());
            try 
            {
                iStream.skip((long) restartBytes);
            } 
            catch (IOException ex)
            {
                Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
            }
            startPlay();
            playt.setStatus("Play");
            playt.setPausedTime(pausePosition);


            runpt.start();
            timept.start();


            isPaused = false;
            firstPress = false;
        }
    }
    
    private void initStop()
    {
        stopFlag = true;
        player.close();
        songNameL.setText("*Song Name*");
        artistL.setText("*Artist*");
        albumL.setText("*Album*");
        timeL.setText("0:0 / 0:00");
        artL.setIcon(new ImageIcon());
        playt.setStatus("Stop");
        try 
        {
            Thread.sleep(1000);
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void initAdd()
    {
        File[] tempAr;
        int returnVal = browse.showOpenDialog(pane);

        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            tempAr = browse.getSelectedFiles();
            for(int i = 0; i < tempAr.length; i++)
            {
                mp3List.add(new MP3File(tempAr[i].getPath()));
            }
            
            strAr = new ArrayList<>();
            for(int i = 0; i < mp3List.size(); i++)
            {
                strAr.add(mp3List.get(i).mp3.getPath());
                mp3List.get(i);
            }
            songList.setListData(strAr.toArray());
            mp3File = mp3List.get(0);
        }
    }
    
    protected void initRemove()
    {
        int selectedIndex = songList.getSelectedIndex();
        if(mp3File.mp3.getPath().equals(mp3List.get(selectedIndex).mp3.getPath()) &&
                "Play".equals(playt.status)) //If the the song being removed is the one that is currently playing,
            initStop();                      //  then stop the song first, which will also remove the song tags from GUI.
        else if (mp3File.mp3.getPath().equals(mp3List.get(selectedIndex).mp3.getPath()))
        {                                   //If the song being removed is not playing, then no need to initStop(), just remove tags.
            songNameL.setText("*Song Name*");
            artistL.setText("*Artist*");
            albumL.setText("*Album*");
            timeL.setText("0:0 / 0:00");
            artL.setIcon(new ImageIcon());
        }
        //Remove the song from the ArrayLists, and reset the JList.
        mp3List.remove(selectedIndex);
        strAr.remove(selectedIndex);
        songList.setListData(strAr.toArray());
    }
    
    protected void initPlay() //created for the ContextMenu class
    {
        if(!mp3List.isEmpty())
        {
            if("Play".equals(playt.status))
                initStop();
            stopFlag = false;
            playMethod(mp3File);
        }
    }
    //-----------------------------
    //End of GUI song manipulation methods
    
    //Other Methods
        //Duration methods used for the song timer.
            //Converts micro/milliseconds to appropriate times in 00:00 format.
        private static String convertToDuration(long microseconds)
        {
            int milli = (int) (microseconds / 1000);
            int sec = (milli / 1000) % 60;
            int min = (milli / 1000) / 60;
            return ("" + min + ":" + sec);
        }

        private static String convertToDuration(int milli)
        {
            int sec = (milli / 1000) % 60;
            int min = (milli / 1000) / 60;
            return ("" + min + ":" + sec);
        }
        //Duration methods end
        private ImageIcon resizeImageIcon(ImageIcon II)
        {
            Image img = II.getImage();
            BufferedImage bi = new BufferedImage(ALBUM_WIDTH, ALBUM_HEIGHT, BufferedImage.TYPE_INT_ARGB);  
            Graphics2D g = bi.createGraphics();  
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(img, 0, 0, ALBUM_WIDTH, ALBUM_HEIGHT, null);
            g.dispose();
            return new ImageIcon(bi);
        }
    //End of Other Methods
    
    public static void main(String[] args)
    {
        MP3Player mp3Player = new MP3Player();
    }
}
