package gov.nist.healthcare.tools;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JScrollPane;

public class WindowsMessageTool {

	private JFrame frmWindowsMessageTool;
	private JSplitPane splitPane;
	private JTree tree;
	private DefaultMutableTreeNode messageFolderTree, scriptFolderTree;
	JTextArea textArea;
	JMenuItem mntmChooseMessageFolder;
	JMenuItem mntmChooseConfigFolder;

	class ContextMenu extends JPopupMenu {
		ContextMenu(String s) { super(s); }
	    public void AddItem(String s){
	    	JMenuItem anItem = new JMenuItem(s);
	        add(anItem);
			anItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					alert("Menu clicked on " + ((JMenuItem)e.getSource()).getText());
				}
			});
	    }
	}
	
    ContextMenu menu = new ContextMenu("Apply config..");
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WindowsMessageTool window = new WindowsMessageTool();
					window.frmWindowsMessageTool.setVisible(true);
					window.frmWindowsMessageTool.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WindowsMessageTool() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWindowsMessageTool = new JFrame();
		frmWindowsMessageTool.setTitle("Windows Message Tool");
		frmWindowsMessageTool.setBounds(100, 100, 929, 651);
		frmWindowsMessageTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmWindowsMessageTool.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmChooseMessageFolder = new JMenuItem("Choose Message Folder");
		mntmChooseMessageFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					setFolder("Message Folder", tree, messageFolderTree, "Message.txt");
				}
		});
		mnFile.add(mntmChooseMessageFolder);
		
		mntmChooseConfigFolder = new JMenuItem("Choose Config Folder");
		mntmChooseConfigFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> addedItems = setFolder("Configs Folder", tree, scriptFolderTree, ".*\\.txt$");
				menu.removeAll();
				for (String s: addedItems)
					menu.AddItem(s);
			}
		});
		mnFile.add(mntmChooseConfigFolder);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About..");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				alert("Windows Message Tool v1.0 - tool for setting predefined values and cross-message validation. \n\nAdd the message folder and if needed, a script folder - then the scripts can be run against the message folder.\n\n\n\n");
			}
		});
		mnHelp.add(mntmAbout);
		
		splitPane = new JSplitPane();
		frmWindowsMessageTool.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		tree = new JTree();
		tree.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("All Folders") {
					{
						add(			
							messageFolderTree = new DefaultMutableTreeNode("Message Folder") {
							{
							}
						}
						);
						add(			
								scriptFolderTree = new DefaultMutableTreeNode("Configs Folder") {
								{
								}
							}
							);
					}
				}
			
		));
		splitPane.setLeftComponent(tree);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		 MouseListener ml = new MouseAdapter() {
		    public void mouseReleased(MouseEvent e){
		        if (e.isPopupTrigger())
		        	contextMenu(e);
		    }

		    private void contextMenu(MouseEvent e){
		        menu.show(e.getComponent(), e.getX(), e.getY());
		    }	
		    
		     public void mousePressed(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            contextMenu(e);
		            return;
		        }
		        
		         int selRow = tree.getRowForLocation(e.getX(), e.getY());
		         TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		         if(selRow != -1) {
		             if(e.getClickCount() == 1) {
		                 singleClick(selRow, selPath);
		             }
		             else if(e.getClickCount() == 2) {
		                 try {
							doubleClick(selRow, selPath);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		             }
		         }
		     }
		 };
		 tree.addMouseListener(ml);
		 
		 mntmChooseMessageFolder.doClick();
		 mntmChooseConfigFolder.doClick();
	}
	
	public void singleClick(int selRow, TreePath selPath) {
		//alert("Single click " + selRow + " " + selPath.toString());
	}

	public void doubleClick(int selRow, TreePath selPath) throws IOException {
		String s = new String(Files.readAllBytes(new File(selPath.getLastPathComponent().toString()).toPath()));
		textArea.setText(s);
	}
	
	public static ArrayList<File> recurseFileList(String path, String match) {

        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return null;
        
        ArrayList<File> fl = new ArrayList<File>();
        
        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	fl.addAll(recurseFileList( f.getAbsolutePath(), match ));
            }
            else {
            	if (match.isEmpty() || match.equals(f.getName()) || f.getName().matches(match)) {
            		fl.add(f.getAbsoluteFile());
            	}
            }
        }
        return fl;
    }

    private static File[] getFileList(String dirPath) {
        File dir = new File(dirPath);   
        
        File[] fileList = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
            	return name.endsWith(".msg");
            }
        });
        return fileList;
    }
    
    private static ArrayList<String> setFolder(String desc, JTree tree, DefaultMutableTreeNode folder, String filter) {
    	Path currentRelativePath = Paths.get("");
    	String s = currentRelativePath.toAbsolutePath().toString();
    	ArrayList<String> addedItems = new ArrayList<String>();
    	
		JFileChooser chooser = new JFileChooser(s);
		chooser.setDialogTitle("Select the " + desc);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {		
			try {
				final String dirpath = chooser.getSelectedFile().getCanonicalPath();
				//final File[] fileList = getFileList(dirpath);
				final ArrayList<File> fileList = recurseFileList(dirpath, filter);
				DefaultMutableTreeNode fileFolder = new DefaultMutableTreeNode(dirpath);
	            for(File file : fileList) {
	            	String label = file.getCanonicalFile().toString();
	                DefaultMutableTreeNode child = new DefaultMutableTreeNode(label);
	                fileFolder.add(child);
	            	addedItems.add(label);
	            }
	            folder.add(fileFolder);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			}
		
		return addedItems;

    }
    private static void alert(String msg) {
    	JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.PLAIN_MESSAGE);
    }
}
