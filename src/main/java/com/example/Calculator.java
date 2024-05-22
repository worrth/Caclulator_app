package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.lang.Math;

import static java.lang.Math.*;

public class Calculator {
    private JFrame frame;
    private JTextField display;
    private String input = "";
    private Stack<Double> memory = new Stack<>();

    public Calculator() {
        frame = new JFrame("com.example.Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        display = new JTextField();
        display.setFont(new Font("Arial", Font.PLAIN, 20));
        display.setEditable(false);
        frame.add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 5));

        String[] buttons = {
                "7", "8", "9", "/", "sqrt",
                "4", "5", "6", "*", "^",
                "1", "2", "3", "-", "=",
                "0", ".", "+/-", "+", "ln",
                "sin", "cos", "tan", "log", "ce"
        };

        for (String button : buttons) {
            JButton btn = new JButton(button);
            btn.setFont(new Font("Arial", Font.PLAIN, 18));
            btn.addActionListener(new ButtonClickListener());
            buttonPanel.add(btn);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if ("0123456789.".contains(command)) {
                input += command;
            } else if ("+-*/^".contains(command)) {
                input += " " + command + " ";
            } else if ("sqrt sin cos tan log ln".contains(command)) {
                input = command + " ( " + input + " ) ";
            } else if ("=".equals(command)) {
                try {
                    input = evaluateExpression(input);
                } catch (ArithmeticException ex) {
                    input = "Error";
                }
            } else if ("+/-".equals(command)) {
                input = negateInput(input);
            }else if("ce".equals((command))){
                input = "";
            }

            display.setText(input);
        }

        private String evaluateExpression(String expression) {
            String result;
            try {
                String[] parts = expression.split(" ");
                Stack<String> operators = new Stack<>();
                Stack<Double> values = new Stack<>();
                List<String> operators_pattern = new ArrayList<>(List.of(
                        "+","-","*","/","^",
                        "sqrt","log","ln","sin",
                        "cos","tan"
                ));
                List<String> numbers_pattern = new ArrayList<>(List.of(
                        "0","1","2","3","4",
                        "5","6","7","8",
                        "9"
                ));
                for (String part : parts) {
                    if (operators_pattern.contains(part)) {
                        operators.push(part);
                    } else if(numbers_pattern.contains(part)) {
                        values.push(Double.parseDouble(part));
                    }

                    while (!operators.isEmpty()) {
                        String operator = operators.pop();
                        if(values.size() >= 1 && "sqrtsincostanlogln".contains(operator)){
                                double a = values.pop();
                                double res = calculate(a, operator);
                                values.push(res);
                        }
                        else if(values.size() >= 2 && "+-*/^".contains(operator)) {
                                double b = values.pop();
                                double a = values.pop();
                                double res = calculate(a, b, operator);
                                values.push(res);
                        }else{
                            operators.push(operator);
                            break;
                        }

                    }
                }

                DecimalFormat df = new DecimalFormat("#.##########");
                result = df.format(values.pop());
            } catch (NumberFormatException | EmptyStackException e) {
                result = "Error";
            }
            return result;
        }

        private double calculate(double a, double b, String operator) {
            switch (operator) {
                case "+":
                    return a + b;
                case "-":
                    return a - b;
                case "*":
                    return a * b;
                case "/":
                    if (b == 0) throw new ArithmeticException("Division by zero");
                    return a / b;
                case "^":
                    return Math.pow(a, b);
                default:
                    throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        }

        private double calculate(double a, String operator){
            switch(operator){
                case "sqrt":
                    if (a < 0) throw new ArithmeticException("Square root of negative number");
                    return sqrt(a);
                case "log":
                    if (a <= 0 ) throw new ArithmeticException("Invalid logarithm");
                    return log10(a);
                case "ln":
                    if (a <= 0 ) throw new ArithmeticException("Invalid logarithm");
                    return log(a);
                case "sin":
                    return sin(a);
                case "cos":
                    return cos(a);
                case "tan":
                    return tan(a);
                default:
                    throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        }

        private String negateInput(String input) {
            if (input.isEmpty()) return input;

            String[] parts = input.split(" ");
            int lastIndex = parts.length - 1;
            String lastPart = parts[lastIndex];

            if (!lastPart.isEmpty() && Character.isDigit(lastPart.charAt(0))) {
                if (lastPart.charAt(0) == '-') {
                    parts[lastIndex] = lastPart.substring(1);
                } else {
                    parts[lastIndex] = "-" + lastPart;
                }
                return String.join(" ", parts);
            } else {
                return input;
            }
        }
    }

}