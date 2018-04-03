package com.danertu.entity;

import android.graphics.Bitmap; 
public class ImageBean {    
	private Bitmap bitmap;   
	private boolean isNeedBar = true;  
	public ImageBean() 
	{         
		super();  
		}      
	public boolean isNeedBar()
	{        
		return isNeedBar; 
		}      
	public void setNeedBar(boolean isNeedBar)
	{   
		this.isNeedBar = isNeedBar;    
		}      
	public Bitmap getBitmap() 
	{        
		return bitmap;  
	}      
	public void setBitmap(Bitmap bitmap)
{     
		this.bitmap = bitmap;   
		}  
	} 
