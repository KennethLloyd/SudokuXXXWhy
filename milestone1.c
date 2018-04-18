#include<stdio.h>
#include<stdlib.h>
#include<string.h>

int main(){
  FILE *fp;
  fp = fopen("input.txt","r");
  int i, j, k, n, counter=0;
  fscanf(fp,"%d",&n);
  char temp[250], *token, delim[2]=" ";
  int subgridSize[n];
  int **puzzles[n];
  
  for(i=0; i<n; i++){
    fscanf(fp,"%d\n",&subgridSize[i]);
    int squaredLen = subgridSize[i]*subgridSize[i];
    puzzles[i] = (int**)malloc(sizeof(int*)*squaredLen);
    for(j=0; j<squaredLen; j++){
      counter = 0;
      puzzles[i][j] = (int*)malloc(sizeof(int)*squaredLen);
      fgets(temp, 250, fp);
      token = strtok(temp, delim);
      while(token!=NULL){
        puzzles[i][j][counter] = atoi(token);
        token = strtok(NULL, delim);
        counter++;
      }
    }
  }
}