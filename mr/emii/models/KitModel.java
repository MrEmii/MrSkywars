package mr.emii.models;

import mr.emii.utils.FileUtils;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitModel {

	private String name;
	private List<ItemStack> items;
	private int price;
	private FileUtils file;

	public KitModel(String name, List<ItemStack> items, int price) {
		this.name = name;
		this.items = items;
		this.price = price;
	}

	public KitModel(String name, List<ItemStack> items, int price, FileUtils file) {
		this.name = name;
		this.items = items;
		this.price = price;
		this.file = file;
	}



	public void setPrice(int price) {
		this.price = price;
	}

	public void setFile(FileUtils file) {
		this.file = file;
	}

	public FileUtils getFile() {
		return file;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setItems(List<ItemStack> items) {
		this.items = items;
	}

	public String getName() {
		return name;
	}

	public List<ItemStack> getItems() {
		return items;
	}

	public int getPrice() {
		return price;
	}
	
}
