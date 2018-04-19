#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<math.h>

void findSolution(int, int **);
int checkPossible(int *, char*, int, int, int, int **);
int compare(const void*, const void*);

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

  for(i=0; i<n; i++){
    for(j=0; j<subgridSize[i]*subgridSize[i]; j++){
      for(k=0; k<subgridSize[i]*subgridSize[i]; k++){
        printf("%d ", puzzles[i][j][k]);
      }
      printf("\n");
    }
    findSolution(subgridSize[i], puzzles[i]);
  }
}

void findSolution(int size, int **puzzle){
  int h, i, j, npossible, start, move, candidate, *candidates;
  int gridTotal = (int)pow(size, 4);    //Total slots in the puzzle
  int rowcolTotal = (int)pow(size, 2);  //Number of elements per move/column
  int noptions[gridTotal+2];
  int options[gridTotal+2][rowcolTotal+2];
  int **grid = puzzle;
  char * solTypes[] = {"regular", "x", "y", "xy"};  //Four Types of Solution (Mutually Exclusive?)
  candidates = (int*) malloc(size * sizeof(int));
  
  for(h=0; h<4; h++){
    if(solTypes[h] == "y" && rowcolTotal%2==0) continue;

    move = start = 0; 
    noptions[start] = 1;
    //Finding Solution with Backtracking
    while(noptions[start] > 0){
      if(noptions[move] > 0){
        move++;
        noptions[move]=0; //initialize new move-1
        if(move == gridTotal+1){ //solution found
          for(i=1;i<move;i++){
            printf("%2i",options[i][noptions[i]]);
            if(i%rowcolTotal == 0) printf("\n");
          }
          printf("\n");
        }
        else if(move == 1){
          npossible = checkPossible(candidates, solTypes[h], rowcolTotal, move/rowcolTotal, (move%rowcolTotal)-1, grid);
          if(npossible == 1){
            options[move][++noptions[move]] = candidates[0];
          }else{
            for(i=1; i<npossible; i++){
              options[move][++noptions[move]] = candidates[i-1];
            }
          }
        }
        else{
          if(move % rowcolTotal == 0) npossible = checkPossible(candidates, solTypes[h], rowcolTotal, (move/rowcolTotal)-1, rowcolTotal-1, grid);
          else npossible = checkPossible(candidates, solTypes[h], rowcolTotal, move/rowcolTotal, (move%rowcolTotal)-1, grid);

          if(npossible == 1){
            options[move][++noptions[move]] = candidates[0];

          }else{
            for(i=1; i<npossible; i++){
              for(j=move-1; j>=1; j--){
                if(move%rowcolTotal == 0){
                  if(j>(move-rowcolTotal) || (j == (move-rowcolTotal))){
                    if(candidates[i-1] == options[j][noptions[j]]) break;
                  }
                }else if(move%rowcolTotal == 1){
                  if((move-j)%rowcolTotal == 0){
                    if(candidates[i-1] == options[j][noptions[j]]) break;
                  }
                }else{
                  if((j%rowcolTotal!=0 && j >= move-rowcolTotal) || ((move-j)%rowcolTotal == 0)){
                    if(candidates[i-1] == options[j][noptions[j]]) break;
                  }
                }
              }
              if(!(j>=1)){
                options[move][++noptions[move]] = candidates[i-1];
              }
            }
          }
        }
        printf("\n\nMOVE %d\n", move);
        for(i=0; i<gridTotal+2; i++){
          printf("%d | ", i);
          for(j=0; j<rowcolTotal+2; j++){
            if(abs(options[i][j]) > 999){
              printf("_ ");
            }else{
              printf("%d ", options[i][j]);
            }
          }
          printf("\n");
        }
        printf("\n");
      }else{
        while(1){
          move--;
          if(move%rowcolTotal == 0){
            if(grid[(move/rowcolTotal)-1][rowcolTotal-1]==0){
              noptions[move]--;
              // printf("move: %d noptions: %d\n", move, noptions[move]);
              break;
            }
          }else{
            if(grid[(move/rowcolTotal)][(move%rowcolTotal)-1]==0){
              noptions[move]--;
              // printf("move: %d noptions: %d\n", move, noptions[move]);
              break;
            }
          }          
        }
      }
    }
    //Break kasi pang regular check palang
    break;
  }
}

int checkPossible(int *candidates, char * solutionType, int size, int row, int col, int ** grid){
  int i, j, flag, counter=0;

  if(grid[row][col] != 0){
    *(candidates+counter) = grid[row][col];
  }else{
    //Initialize everything to -1 and use it as indicator of true candidates
    for(i=0; i<size; i++) candidates[i] = -1;

    for(i=size; i>0; i--){    //PRESORTS IN DESCENDING ORDER
      flag = 1;
      for(j=0; j<size; j++){
        if(solutionType == "regular"){
          if(grid[row][j] == i || grid[j][col] == i){
            flag = 0;
            break;
          }
        }else if(solutionType == "x"){

        }else if(solutionType == "y"){
          
        }else if(solutionType == "xy"){

        }
      }
      if(flag == 1){
        *(candidates+counter) = i;
        counter++;
      }
    }
  }

  return counter+1;
}