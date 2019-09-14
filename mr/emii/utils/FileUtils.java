package mr.emii.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileUtils extends YamlConfiguration{

	private String filename, path;
	private File file;
	
	public FileUtils(String filename, String path) {
	    String ffil = filename.endsWith(".yml") ? filename : filename.concat(".yml");
		this.filename = ffil;
		this.path = path;
		this.file = new File((path.endsWith("/") ? path: path+"/") +this.filename);
	}

	public FileUtils(File file) {
		String ffil = file.getName().endsWith(".yml") ? file.getName() : file.getName().concat(".yml");
		this.filename = ffil;
		this.path = file.getAbsolutePath();
		this.file = file;
	}

	@Override
	public void set(String path, Object value) {		
		super.set(path, value);
		try {
			this.save(this.file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getFilename() {
		return filename;
	}

	public String getPath() {
		return path;
	}

	public boolean createFile(){
		if(!this.file.exists()) {
			if(!this.file.getParentFile().exists()) {
				this.file.getParentFile().mkdirs();
			}
			try {
				this.file.createNewFile();
				this.load(this.file);
				this.save(this.file);
				return true;
			}
			catch(IOException e) {
				e.printStackTrace();
				return false;
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}else{
			try {
				this.load(this.file);
				this.save(this.file);
			}
			catch(IOException e) {
				e.printStackTrace();

			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
		return false;
	}

	public void save(){
		try {
			this.save(this.file);
			this.load(this.file);
		}
		catch(IOException e) {
			e.printStackTrace();

		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	public void reload(){
		try {
			this.load(this.file);
			this.save(this.file);
		}
		catch(IOException e) {
			e.printStackTrace();

		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	public File getFile() {
		return this.file;
	}
}
