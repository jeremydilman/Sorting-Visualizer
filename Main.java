import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class Main {
    private static int[] array = new int[10];
    private static DrawingCanvas canvas;
    private static JComboBox<String> algorithmSelector;
    private static JComboBox<String> dataTypeSelector;
    private static JButton startButton, nextButton, playButton;
    private static JLabel stepLabel;
    private static Timer timer;
    private static int stepCount = 0;

    // Bubble/Insertion/Selection
    private static int i = 0, j = 0;
    private static boolean sorted = false;

    // Insertion flags
    private static boolean comparing = false;
    private static boolean inserting = false;
    private static int key;

    // Merge Sort
    private static List<int[]> mergeSteps;
    private static List<int[]> groupSteps;
    private static int mergeStepIndex;
    private static int[] groupColors;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sorting Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        canvas = new DrawingCanvas(array);
        frame.add(canvas, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();

        algorithmSelector = new JComboBox<>(new String[]{"Bubble Sort", "Insertion Sort", "Selection Sort", "Merge Sort"});
        dataTypeSelector = new JComboBox<>(new String[]{"Random", "Partially Sorted", "Reverse Order"});
        JTextField sizeField = new JTextField("10", 3);
        startButton = new JButton("Start");
        nextButton = new JButton("Next");
        playButton = new JButton("Play");
        stepLabel = new JLabel("Steps: 0");

        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algorithmSelector);
        controlPanel.add(new JLabel("Data:"));
        controlPanel.add(dataTypeSelector);
        controlPanel.add(new JLabel("Size:"));
        controlPanel.add(sizeField);
        controlPanel.add(startButton);
        controlPanel.add(nextButton);
        controlPanel.add(playButton);
        controlPanel.add(stepLabel);

        frame.add(controlPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> {
            try {
                int size = Integer.parseInt(sizeField.getText());
                if (size < 2 || size > 100) {
                    JOptionPane.showMessageDialog(frame, "Choose size between 2 and 100.");
                    return;
                }
                array = new int[size];
                String dataType = (String) dataTypeSelector.getSelectedItem();
                switch (dataType) {
                    case "Random":
                        generateRandomArray();
                        break;
                    case "Partially Sorted":
                        generatePartiallySortedArray();
                        break;
                    case "Reverse Order":
                        generateReverseOrderArray();
                        break;
                }

                currentAlgorithm = (String) algorithmSelector.getSelectedItem();
                i = j = 0;
                stepCount = 0;
                stepLabel.setText("Steps: 0");
                sorted = false;
                canvas.setHighlightIndices(-1, -1);

                if (currentAlgorithm.equals("Merge Sort")) {
                    mergeSteps = new ArrayList<>();
                    groupSteps = new ArrayList<>();
                    groupColors = new int[array.length];
                    for (int g = 0; g < array.length; g++) {
                        groupColors[g] = g;
                    }

                    int[] arrayCopy = array.clone();
                    int[] groupCopy = groupColors.clone();
                    mergeSteps.add(arrayCopy.clone());
                    groupSteps.add(groupCopy.clone());

                    iterativeMergeSort(arrayCopy, groupCopy);
                    mergeStepIndex = 0;
                    array = mergeSteps.get(mergeStepIndex).clone();
                    canvas.setArray(array);
                    canvas.setGroups(groupSteps.get(mergeStepIndex).clone());
                } else {
                    canvas.setGroups(null);
                    canvas.setArray(array);
                }

                canvas.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid integer for size.");
            }
        });

        nextButton.addActionListener(e -> stepForward());

        playButton.addActionListener(e -> {
            if (timer != null && timer.isRunning()) {
                timer.stop();
                playButton.setText("Play");
            } else {
                timer = new Timer(300, evt -> stepForward());
                timer.start();
                playButton.setText("Pause");
            }
        });

        frame.setVisible(true);
    }

    private static String currentAlgorithm = "Bubble Sort";

    private static void stepForward() {
        if (sorted) return;

        boolean changed = false;
        switch (currentAlgorithm) {
            case "Bubble Sort":
                changed = bubbleSortStep();
                break;
            case "Insertion Sort":
                changed = insertionSortStep();
                break;
            case "Selection Sort":
                changed = selectionSortStep();
                break;
            case "Merge Sort":
                changed = mergeSortStep();
                break;
        }

        if (changed) {
            stepCount++;
            stepLabel.setText("Steps: " + stepCount);
        }

        canvas.repaint();
    }

    private static boolean bubbleSortStep() {
        if (i < array.length - 1) {
            if (j < array.length - i - 1) {
                canvas.setHighlightIndices(j, j + 1);
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
                j++;
                return true;
            } else {
                j = 0;
                i++;
                return true;
            }
        }
        canvas.setHighlightIndices(-1, -1);
        sorted = true;
        return true;
    }

    private static boolean insertionSortStep() {
        if (!inserting) {
            if (i >= array.length) {
                sorted = true;
                return false;
            }
            key = array[i];
            j = i - 1;
            inserting = true;
            return true;
        }

        if (j >= 0) {
            canvas.setHighlightIndices(j, j + 1);
            if (array[j] > key) {
                array[j + 1] = array[j];
                j--;
                return true;
            } else {
                array[j + 1] = key;
                inserting = false;
                i++;
                return true;
            }
        } else {
            array[j + 1] = key;
            inserting = false;
            i++;
            return true;
        }
    }

    private static int minIndex = -1;
    private static boolean selectionSortStep() {
        if (i >= array.length - 1) {
            sorted = true;
            return false;
        }
        if (j == 0) {
            minIndex = i;
            j = i + 1;
        }

        if (j < array.length) {
            canvas.setHighlightIndices(minIndex, j);
            if (array[j] < array[minIndex]) {
                minIndex = j;
            }
            j++;
            return true;
        } else {
            if (minIndex != i) {
                int temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
            }
            i++;
            j = 0;
            return true;
        }
    }

    private static boolean mergeSortStep() {
        if (mergeStepIndex + 1 < mergeSteps.size()) {
            mergeStepIndex++;
            array = mergeSteps.get(mergeStepIndex).clone();
            canvas.setArray(array);
            canvas.setGroups(groupSteps.get(mergeStepIndex).clone());
            return true;
        }
        return false;
    }

    private static void iterativeMergeSort(int[] arr, int[] groups) {
        int n = arr.length;
        int[] nextGroupId = {n};
        for (int currSize = 1; currSize < n; currSize *= 2) {
            for (int leftStart = 0; leftStart < n - 1; leftStart += 2 * currSize) {
                int mid = Math.min(leftStart + currSize - 1, n - 1);
                int rightEnd = Math.min(leftStart + 2 * currSize - 1, n - 1);
                merge(arr, leftStart, mid, rightEnd, groups, nextGroupId);
            }
        }
    }

    private static void merge(int[] arr, int l, int m, int r, int[] groups, int[] nextGroupId) {
        int n1 = m - l + 1;
        int n2 = r - m;
        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; i++) L[i] = arr[l + i];
        for (int j = 0; j < n2; j++) R[j] = arr[m + 1 + j];

        int i = 0, j = 0, k = l;
        int newGroup = nextGroupId[0]++;
        for (int idx = l; idx <= r; idx++) groups[idx] = newGroup;

        while (i < n1 && j < n2) {
            arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
            mergeSteps.add(arr.clone());
            groupSteps.add(groups.clone());
        }
        while (i < n1) {
            arr[k++] = L[i++];
            mergeSteps.add(arr.clone());
            groupSteps.add(groups.clone());
        }
        while (j < n2) {
            arr[k++] = R[j++];
            mergeSteps.add(arr.clone());
            groupSteps.add(groups.clone());
        }
    }

    private static void generateRandomArray() {
        Random rand = new Random();
        for (int k = 0; k < array.length; k++) {
            array[k] = rand.nextInt(400) + 50;
        }
    }

    private static void generatePartiallySortedArray() {
        for (int k = 0; k < array.length; k++) {
            array[k] = 50 + (k * 400 / array.length);
        }
        Random rand = new Random();
        for (int k = (int)(array.length * 0.75); k < array.length; k++) {
            int randIndex = rand.nextInt(array.length);
            int temp = array[k];
            array[k] = array[randIndex];
            array[randIndex] = temp;
        }
    }

    private static void generateReverseOrderArray() {
        for (int k = 0; k < array.length; k++) {
            array[k] = 450 - (k * 400 / array.length);
        }
    }
}
