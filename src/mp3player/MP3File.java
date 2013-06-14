/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3player;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;
// import sun.org.mozilla.javascript.internal.ast.TryStatement;
import sun.security.x509.AuthorityInfoAccessExtension;

/**
 *
 * @author DOOMIE
 */
public class MP3File
{
    File mp3;
    AudioFile AFmp3;
    Tag AFTag;
    fileDirectory mp3Directory;
    Song songStr;
    Author authorStr;
    Album albumStr;
    AudioFileFormat baseFileFormat = null;
    AudioFormat baseFormat = null;
    Artwork albumArt;
    
    public MP3File(String filename)
    {
        createFile(filename);
    }
    
    public void createFile(String filename)
    {
        mp3 = new File(filename);
        //Create AudioFile
        try 
        {
            AFmp3 = new AudioFileIO().readFile(mp3);
        } 
        catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex) 
        {
            Logger.getLogger(MP3File.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Create Tag
        AFTag = AFmp3.getTag();
        try 
        {
            baseFileFormat = AudioSystem.getAudioFileFormat(mp3);
        }
        catch (UnsupportedAudioFileException | IOException ex) 
        {
            Logger.getLogger(MP3File.class.getName()).log(Level.SEVERE, null, ex);
        }
        baseFormat = baseFileFormat.getFormat();
    }
    
    public String getTitle(Map properties, InputStream tag)
    {
        songStr = new Song(properties, tag);
        return songStr.getName();
    }
    
    public String getAuthor(Map properties, InputStream tag)
    {
        authorStr = new Author(properties, tag);
        return authorStr.getName();
    } 
   
    public String getAlbum(Map properties, InputStream tag)
    {
        albumStr = new Album(properties, tag);
        return albumStr.getName();
    }
    
    public BufferedImage getArtwork()
    {
        albumArt = AFTag.getFirstArtwork();
        BufferedImage art = null;
        if(albumArt != null)
        {
            try 
            {
                //System.out.println("Crash spot 1?");
                art = (BufferedImage) albumArt.getImage();
                //System.out.println("Crash spot 2?");
            } 
            catch (Exception ex) 
            {
                //Logger.getLogger(MP3File.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return art;
    }
    
    public String getDate(Map properties, InputStream tag)
    {
        String val = "";
        if (baseFileFormat instanceof TAudioFileFormat)
        {
            properties = ((TAudioFileFormat)baseFileFormat).properties();
            String key = "date";
            val = (String) properties.get(key);
            key = "mp3.id3tag.v2";
            tag = (InputStream) properties.get(key);
        }
        return val;
    }
    
    public String getCopyright(Map properties, InputStream tag)
    {
        String val = "";
        if (baseFileFormat instanceof TAudioFileFormat)
        {
            properties = ((TAudioFileFormat)baseFileFormat).properties();
            String key = "copyright";
            val = (String) properties.get(key);
            key = "mp3.id3tag.v2";
            tag = (InputStream) properties.get(key);
        }
        return val;
    }
    
    public String getComment(Map properties, InputStream tag)
    {
        String val = "";
        if (baseFileFormat instanceof TAudioFileFormat)
        {
            properties = ((TAudioFileFormat)baseFileFormat).properties();
            String key = "comment";
            val = (String) properties.get(key);
            key = "mp3.id3tag.v2";
            tag = (InputStream) properties.get(key);
        }
        return val;
    }
    
    public Long getDuration(Map properties, InputStream tag)
    {
        Long val = null;
        if (baseFileFormat instanceof TAudioFileFormat)
        {
            properties = ((TAudioFileFormat)baseFileFormat).properties();
            String key = "duration";
            val = (Long) properties.get(key);
            key = "mp3.id3tag.v2";
            tag = (InputStream) properties.get(key);
        }
        return val;
    }
    
    public class Song
    {
        String name;
        
        public Song(Map properties, InputStream tag)
        {
            String val = "";
            if (baseFileFormat instanceof TAudioFileFormat)
            {
                properties = ((TAudioFileFormat)baseFileFormat).properties();
                String key = "title";
                name = (String) properties.get(key);
                key = "mp3.id3tag.v2";
                tag = (InputStream) properties.get(key);
            }
        }
        
        public String getName()
        {
            return name;
        }
    }
    
    public class Author
    {
        String name;
        
        public Author(Map properties, InputStream tag)
        {
            String val = "";
            if (baseFileFormat instanceof TAudioFileFormat)
            {
                properties = ((TAudioFileFormat)baseFileFormat).properties();
                String key = "author";
                name = (String) properties.get(key);
                key = "mp3.id3tag.v2";
                tag = (InputStream) properties.get(key);
            }
        }
        
        public String getName()
        {
            return name;
        }
    }
    
    public class Album
    {
        String name;
        
        public Album(Map properties, InputStream tag)
        {
            String val = "";
            if (baseFileFormat instanceof TAudioFileFormat)
            {
                properties = ((TAudioFileFormat)baseFileFormat).properties();
                String key = "album";
                name = (String) properties.get(key);
                key = "mp3.id3tag.v2";
                tag = (InputStream) properties.get(key);
            }
        }
        
        public String getName()
        {
            return name;
        }
    }
    
    public class fileDirectory
    {
        String directory;
        public fileDirectory(File mp3)
        {
            directory = mp3.getPath();
        }
        
        public String getDirectory()
        {
            return directory;
        }
    }
}
