package Structures;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Stack;

import main.MyScreen;

public class Trie {
	private boolean isMarked;
	private Edge[] edges = new Edge[26];

	public Trie(){}

	public void setMarked(){
		isMarked = true;
	}
	
	public Trie addChar(char c){
		if(edges[getIndex(c)] != null){
			//do nothing, already created
		}else{
			edges[getIndex(c)] = new Edge(this, new Trie(), c);
		}
		return edges[getIndex(c)].getB();
	}

	public boolean isMarked(){
		return isMarked;
	}
	
	public void drawTrie(NodeHandler nh){	
		int[] depthArray;
		ArrayList<DepthInfo> depth = this.getDepthList(1);

		//get max depth from depth information
		int maxDepth = 0;
		for(int i = 0; i < depth.size(); i++){
			if(depth.get(i).level > maxDepth)
				maxDepth = depth.get(i).level;
		}
		
		//create array with number of nodes per level
		depthArray = new int[maxDepth];
		for(int i = 0; i < depth.size(); i++){
			DepthInfo di = depth.get(i);
			depthArray[di.level - 1] += di.count;
		}
		
		nh.setDepthInfo(depthArray, this);
	}

	private ArrayList<DepthInfo> getDepthList(int level){
		ArrayList<DepthInfo> depth = new ArrayList<DepthInfo>();
		for(int i = 0; i <  this.getEdges().length; i++){
			if(getEdges()[i] != null){
				ArrayList<DepthInfo> rec = getEdges()[i].getB().getDepthList(level + 1);
				depth.addAll(rec);
			}
		}
		depth.add(new DepthInfo(level, 1));
		return depth;
	}
	
	private class DepthInfo{
		int level;
		int count;
		
		public DepthInfo(int l, int c){
			level = l;
			count = c;
			//System.out.println(this.toString());
		}
		
		public String toString(){
			return "("+level+", "+count+")";
		}
	}
	
	public static void createExample(){
		Trie root = new Trie();
		String[] strings = {"barfoo", "foobar", "foo", "bar", "barfuss", "fool"};

		for(int i = 0; i < strings.length; i++){
			Trie temp = root;
			for(int j = 0; j < strings[i].length(); j++){
				temp = temp.addChar(strings[i].charAt(j));
			}
			temp.setMarked();
		}
		
		//Trie.printTrie(root);
	}
	
	public Edge[] getEdges(){
		return edges;
	}

	private int getIndex(char c){
		return ((int) c ) - 97;
	}
	
	private static void printTrie(Trie t){
		Stack<TrieString> stack = new Stack<TrieString>();
		stack.push(new TrieString(t, ""));
		// stack−based depth−first search with output in preorder
		while (!stack.isEmpty()){
			TrieString ts = stack.pop();
			Trie n = ts.t;
			if(n != null){
				if(n.isMarked())
					System.out.println(ts.s);
				for(int i = 0; i < n.getEdges().length; i++){
					Edge e = n.getEdges()[i];
					if(e != null){
						stack.push(new TrieString(e.getB(), ts.s + e.getC()));
					}
				}
			}
		}
	}
	
	private static class TrieString{
		public String s;
		public Trie t;
		
		public TrieString(Trie t, String s){
			this.s = s;
			this.t = t;
		}
	}
	
	public void paintNode(Graphics g, int level, int nrInLevel, int[] depthInfo, int height){
		g.setColor(Color.gray);
		if(this.isMarked)
			g.setColor(Color.green);
		g.drawRect(800/(depthInfo[level]+1) * nrInLevel, height * level, 16, 16);
		
		g.setColor(Color.gray);
		int nr = 1;
		for(int i = 0; i < getEdges().length; i++){
			Edge e = getEdges()[i];
			if(e != null){
				String s = ""+e.getC();
				if(level != depthInfo.length - 1 && s != null){
					int ox = 800/(depthInfo[level]+1) * nrInLevel + 8;
					int oy = height * level + 16;
					int nx = 800/(depthInfo[level + 1]+1) * nr + 8;
					int ny = height * (level + 1);
					g.drawLine(ox, oy, nx, ny);
					g.drawString(s, (ox + nx) / 2, (oy + ny) / 2);
				}
				e.getB().paintNode(g, level + 1, nr, depthInfo, height);
				nr ++;
			}
		}
	}
}
