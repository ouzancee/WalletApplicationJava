package com.example.walletapplication.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletapplication.R;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying backup files in a RecyclerView
 */
public class BackupFilesAdapter extends RecyclerView.Adapter<BackupFilesAdapter.BackupFileViewHolder> {
    
    private List<String> backupFiles;
    private final OnBackupFileSelectedListener listener;
    
    public interface OnBackupFileSelectedListener {
        void onBackupFileSelected(String filePath);
    }
    
    public BackupFilesAdapter(List<String> backupFiles, OnBackupFileSelectedListener listener) {
        this.backupFiles = backupFiles;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public BackupFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_backup_file, parent, false);
        return new BackupFileViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BackupFileViewHolder holder, int position) {
        String filePath = backupFiles.get(position);
        holder.bind(filePath, listener);
    }
    
    @Override
    public int getItemCount() {
        return backupFiles.size();
    }
    
    public void updateFiles(List<String> newFiles) {
        this.backupFiles = newFiles;
        notifyDataSetChanged();
    }
    
    static class BackupFileViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewFileName;
        private final TextView textViewFileDate;
        private final TextView textViewFileSize;
        private final MaterialButton buttonRestore;
        
        public BackupFileViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
            textViewFileDate = itemView.findViewById(R.id.textViewFileDate);
            textViewFileSize = itemView.findViewById(R.id.textViewFileSize);
            buttonRestore = itemView.findViewById(R.id.buttonRestore);
        }
        
        public void bind(String filePath, OnBackupFileSelectedListener listener) {
            File file = new File(filePath);
            
            // Set file name
            textViewFileName.setText(file.getName());
            
            // Set file date
            Date lastModified = new Date(file.lastModified());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            textViewFileDate.setText(dateFormat.format(lastModified));
            
            // Set file size
            long fileSizeInBytes = file.length();
            String fileSize = formatFileSize(fileSizeInBytes);
            textViewFileSize.setText(fileSize);
            
            // Set click listener
            buttonRestore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBackupFileSelected(filePath);
                }
            });
        }
        
        private String formatFileSize(long bytes) {
            if (bytes < 1024) {
                return bytes + " B";
            } else if (bytes < 1024 * 1024) {
                return String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0);
            } else {
                return String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0));
            }
        }
    }
} 