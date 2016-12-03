import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by manoel on 02/12/2016.
 */
public class FileExtensionFilter implements FilenameFilter {

    private String[] extensions;
    /** Constructor of a file extension filter
     * @param exts Extensions format to be filtered from a directory*/
    public FileExtensionFilter(String... exts){
        extensions = exts;
    }

    @Override
    public boolean accept(File dir, String name) {
        for(String ext : extensions)
            if(name.endsWith(ext))
                return true;

        return false;
    }
}
