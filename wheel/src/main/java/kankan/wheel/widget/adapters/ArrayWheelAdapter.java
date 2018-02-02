/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package kankan.wheel.widget.adapters;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

/**
 * The simple Array wheel adapter
 * @param <T> the element type
 */
public class ArrayWheelAdapter<T> extends AbstractWheelTextAdapter {
    
    // items
    private T items[];
    private  String bitItems[];
    private String couponsBit[];
    private String productBit, couponBit;
    private int thisIndex = 0;
    /**
     * Constructor
     * @param context the current context
     * @param items the items
     */
    public ArrayWheelAdapter(Context context, T items[], String bitItems[], String couponBitItems[]) {
        super(context);
        
        //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.items = items;
        this.bitItems  = bitItems;
        this.couponsBit = couponBitItems;
    }
   
    
   /* public ArrayWheelAdapter(Context context, T items[], String productBit, String couponBit) {
        super(context);
        
        //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.items = items;
        this.productBit  = productBit;
        this.couponBit   = couponBit;
    }*/
   
    @Override
    public CharSequence getItemText(int index) {
    	
        if (index >= 0 && index < items.length) {
            T item = items[index];
            thisIndex = index;
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }
    
    
    public String getProductBit() {
		return bitItems[thisIndex];
	}
    
    public String getCouponBit() {
		return couponsBit[thisIndex];
	}
    @Override
    protected void configureTextView(TextView view) {
    	
    	super.configureTextView(view);
    	
    	if(getCouponBit().equalsIgnoreCase("true")){
    		
    		view.setTextColor(Color.rgb(48,85,4));
    	}
 
    	
    	if(getProductBit().equalsIgnoreCase("1")){
    		
    		view.setTextColor(0xffff0000);
    			
    	} 	
    	
     	
    }

    @Override
    public int getItemsCount() {
    	
        return items.length;
    }
}
