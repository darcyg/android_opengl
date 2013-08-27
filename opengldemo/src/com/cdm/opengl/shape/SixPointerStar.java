package com.cdm.opengl.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cdm.opengl.view.MySurfaceView;

public class SixPointerStar {
	
	public static float[] mProjMatrix = new float[16]; //4X4投影矩阵
	public static float[] mVMatrix = new float[16];//摄像机位置朝向的参数矩阵
	public static float[] mMVPMatrix ;//总变换矩阵
	int mProgram;//自定义渲染管线着色器程序id
	int muMVPMatrixHandle; //总变换矩阵引用
	int maPositionHandle; //顶点位置属性引用
	int maColorHandle;//顶点颜色属性引用
	String mVertexShader;//顶点着色器代码脚本
	String mFragmentShader;//片元着色器脚本
	static float[] mMMatrix = new float[16];
	
	FloatBuffer mVertexBuffer;//
	FloatBuffer mColorBuffer;
	int vCount = 0;//顶点数量
	
	public float yAngle = 0;
	public float XAngle = 0;
	final float UNIT_SIZE = 1;
	
	public SixPointerStar(MySurfaceView mv,float r,float R,float z){
		initVertexData(R, r, z);
		initShader(mv);
	}
	
	public void initVertexData(float R,float r,float z){
		List<Float> flist = new ArrayList<Float>();
		float tempAngle = 360 / 6;
		for(float angle=0;angle<360;angle+=tempAngle){
			flist.add(0f);flist.add(0f);flist.add(0f);
			
			flist.add((float)(R*UNIT_SIZE*Math.cos(Math.toRadians(angle))));
			flist.add((float)(R*UNIT_SIZE*Math.sin(Math.toRadians(angle))));
			flist.add(z);
			
			flist.add((float)(r*UNIT_SIZE*Math.cos(Math.toRadians(angle+tempAngle/2))));
			flist.add((float)(r*UNIT_SIZE*Math.sin(Math.toRadians(angle+tempAngle/2))));
			flist.add(z);
			
			flist.add(0f);flist.add(0f);flist.add(0f);
			
			flist.add((float)(r*UNIT_SIZE*Math.cos(Math.toRadians(angle+tempAngle/2))));
			flist.add((float)(r*UNIT_SIZE*Math.sin(Math.toRadians(angle+tempAngle/2))));
			flist.add(z);
			
			flist.add((float)(r*UNIT_SIZE*Math.cos(Math.toRadians(angle+tempAngle))));
			flist.add((float)(r*UNIT_SIZE*Math.sin(Math.toRadians(angle+tempAngle))));
			flist.add(z);
		}
		vCount = flist.size() / 3;
		float[] vertexArray = new float[flist.size()];
		for(int i=0;i<vCount;i++){
			vertexArray[i*3] = flist.get(i*3);
			vertexArray[i*3+1] = flist.get(i*3+1);
			vertexArray[i*3+2] = flist.get(i*3+2);
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertexArray);
		mVertexBuffer.position(0);
		
		float[] colorArray = new float[vCount*4];
		for(int i=0;i<vCount;i++){
			if(i%3==0){
				colorArray[i*4] = 1;
				colorArray[i*4+1] = 1;
				colorArray[i*4+2] = 1;
				colorArray[i*4+3] = 0;
			}else{
				colorArray[i*4] = 0.45f;
				colorArray[i*4+1] = 0.75f;
				colorArray[i*4+2] = 0.75f;
				colorArray[i*4+3] = 0;
			}
		}
		
		ByteBuffer cbb = ByteBuffer.allocateDirect(colorArray.length*4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colorArray);
		mColorBuffer.position(0);
	}
	
	public void initShader(MySurfaceView mv){
		
	}
	
	public void drawSelf(){
		GLES20.glUseProgram(mProgram);
		Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
		Matrix.translateM(mMMatrix, 0, 0, 0, 1);
		Matrix.rotateM(mMMatrix, 0, yAngle, 0, 1, 0);
		Matrix.rotateM(mMMatrix, 0, XAngle, 1, 0, 0);
		
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, Triangle.getFinalMatrix(mMMatrix), 0);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, mVertexBuffer);
		GLES20.glVertexAttribPointer(maColorHandle, 4,GLES20.GL_FLOAT, false, 4*4, mColorBuffer);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maColorHandle);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}

}
