package leeapp.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;

public class Company {
	
	public void createCompany(Config company, String name, CommandSender sender){
		company.set("company", "");
		company.set("company." + name, "");
		company.set("company." + name + ".owner", sender.getName().toLowerCase());
		company.set("company." + name + ".subOwner", "");
		company.set("company." + name + ".fund", company.getLong("createMoney"));
		company.set("company." + name + ".amount", 0);
		ArrayList<String> list = new ArrayList<>();
		list.add(sender.getName().toLowerCase());
		company.set("company." + name + ".list", list);
		company.save();
	}
	
	public int getCreateMoney(Config company){
		try {
			return company.getInt("createMoney");
		} catch (Exception e) {
			return -1;
		}
	}
	
	public void removeCompany(Config company, String name){
		company.remove(name);
		company.save();
	}
	
	public String[] getCompanyList(Config company){
		String[] list = new String[company.getSection("company").entrySet().size()];
		int i = 0;
		for(Entry<String, Object> l : company.getSection("company").entrySet()){
			list[i] = l.getKey();
			i++;
		}
		return list;
	}
	
	public boolean containCompany(Config company, String companyName){
		for(Entry<String, Object> list : company.getSections("company").getAll().entrySet()){
			if(list.getKey().equals(companyName)){
				return true;
			}
		}
		return false;
	}
	
	public String getUserCompany(Player user, Config company){
		String name = user.getName().toLowerCase();
		for(Entry<String, Object> list : company.getSections("company").getAll().entrySet()){
			if(company.getStringList("company." + list.getKey() + ".list").contains(name)){
				return list.getKey();
			}
		}
		return "¹«¼Ò¼Ó";
	}
	
	public String[] getList(String companyName, Config company){
		String[] list = new String[company.getStringList("company." + companyName + ".list").size()];
		for(int i = 0; i < company.getStringList("company." + companyName + ".list").size(); i++){
			list[i] = company.getStringList("company." + companyName + ".list").get(i);
		}
		return list;
	}
	
	public int getOnlineList(Server server, String companyName, Config company){
		int i = 0;
		for(String name : getList(companyName, company)){
			Player user = server.getPlayer(name);
			if(user != null){
				i++;
			}
		}
		return i;
	}
	
	public void addList(String companyName, Player user, Config company){
		String name = user.getName().toLowerCase();
		List<String> list = company.getStringList("company." + companyName + ".list");
		list.add(name);
		company.set("company." + companyName + ".list", list);
		company.save();
	}
	
	public void delList(String companyName, Player user, Config company){
		String name = user.getName().toLowerCase();
		List<String> list = company.getStringList("company." + companyName + ".list");
		list.remove(name);
		company.set("company." + companyName + ".list", list);
		company.save();
	}
	
	public void setSubOwner(String companyName, Player user, Config company){
		String name = user.getName().toLowerCase();
		company.set("company." + companyName + ".subOwner", name);
		company.save();
	}
	
	public String getSubOwner(String companyName, Config company){
		return company.getString("company." + companyName + ".subOwner");
	}
	
	public boolean isSubOwner(Player user, Config company){
		String name = user.getName().toLowerCase();
		for(Entry<String, Object> list : company.getSections("company").getAll().entrySet()){
			if(company.getString("company." + list.getKey() + ".subOwner").equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public void setOwner(String companyName, Player user, Config company){
		String name = user.getName().toLowerCase();
		company.set("company." + companyName + ".owner", name);
		company.save();
	}
	
	public String getOwner(String companyName, Config company){
		return company.getString("company." + companyName + ".owner");
	}
	
	public boolean isOwner(Player user, Config company){
		String name = user.getName().toLowerCase();
		for(Entry<String, Object> list : company.getSections("company").getAll().entrySet()){
			if(company.getString("company." + list.getKey() + ".owner").equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isCompanyBeing(String name, Config company){
		for(String n : getCompanyList(company)){
			if(n == name){
				return true;
			}
		}
		
		return false;
	}
	
	public long getFund(String name, Config company){
		return company.getInt("company." + name + ".fund");
	}
	
	public void addFund(long amount, String name, Config company){
		long x = company.getInt("company." + name + ".fund") + amount;
		company.set("company." + name + ".fund", x);
		company.save();
	}
	
	public void reduceFund(long amount, String name, Config company){
		long x = company.getInt("company." + name + ".fund") - amount;
		company.set("company." + name + ".fund", x);
		company.save();
		
	}
	
	public long getAmount(String name, Config company){
		return company.getLong("company." + name + ".amount");
	}
	
	public void setAmount(String name, long amount, Config company){
		company.set("company." + name + ".amount", amount);
		company.save();
	}
}
