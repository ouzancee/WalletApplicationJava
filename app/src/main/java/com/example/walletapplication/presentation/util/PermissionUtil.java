package com.example.walletapplication.presentation.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for handling runtime permissions in a clean and reusable way
 */
public class PermissionUtil {
    
    /**
     * Common permission groups for easy access
     */
    public static class Permissions {
        // Storage permissions
        public static final String[] STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        
        // Camera permissions
        public static final String[] CAMERA = {
                Manifest.permission.CAMERA
        };
        
        // Location permissions
        public static final String[] LOCATION = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        
        // Contacts permissions
        public static final String[] CONTACTS = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };
        
        // Phone permissions
        public static final String[] PHONE = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE
        };
    }
    
    /**
     * Callback interface for permission results
     */
    public interface PermissionCallback {
        /**
         * Called when all requested permissions are granted
         */
        void onPermissionsGranted();
        
        /**
         * Called when one or more permissions are denied
         * @param deniedPermissions List of denied permissions
         */
        void onPermissionsDenied(List<String> deniedPermissions);
        
        /**
         * Called when the user selects "Don't ask again" for some permissions
         * @param permanentlyDeniedPermissions List of permanently denied permissions
         */
        default void onPermissionsPermanentlyDenied(List<String> permanentlyDeniedPermissions) {
            // Default implementation - can be overridden
            onPermissionsDenied(permanentlyDeniedPermissions);
        }
    }
    
    /**
     * Helper class to manage permissions for a Fragment
     */
    public static class PermissionManager {
        private final Fragment fragment;
        private final ActivityResultLauncher<String[]> permissionLauncher;
        private PermissionCallback currentCallback;
        
        public PermissionManager(Fragment fragment) {
            this.fragment = fragment;
            this.permissionLauncher = fragment.registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    this::handlePermissionResult
            );
        }
        
        /**
         * Checks if all permissions are granted
         * @param permissions Array of permissions to check
         * @return true if all permissions are granted, false otherwise
         */
        public boolean hasPermissions(String[] permissions) {
            return PermissionUtil.hasPermissions(fragment.requireContext(), permissions);
        }
        
        /**
         * Requests permissions and handles the result via callback
         * @param permissions Array of permissions to request
         * @param callback Callback to handle the result
         */
        public void requestPermissions(String[] permissions, PermissionCallback callback) {
            this.currentCallback = callback;
            
            // Filter permissions that need to be requested
            String[] permissionsToRequest = getPermissionsToRequest(permissions);
            
            if (permissionsToRequest.length == 0) {
                // All permissions already granted
                callback.onPermissionsGranted();
                return;
            }
            
            // Request permissions
            permissionLauncher.launch(permissionsToRequest);
        }
        
        /**
         * Requests storage permissions specifically
         * @param callback Callback to handle the result
         */
        public void requestStoragePermissions(PermissionCallback callback) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ doesn't need storage permissions for scoped storage
                callback.onPermissionsGranted();
            } else {
                requestPermissions(Permissions.STORAGE, callback);
            }
        }
        
        /**
         * Requests camera permissions
         * @param callback Callback to handle the result
         */
        public void requestCameraPermissions(PermissionCallback callback) {
            requestPermissions(Permissions.CAMERA, callback);
        }
        
        /**
         * Requests location permissions
         * @param callback Callback to handle the result
         */
        public void requestLocationPermissions(PermissionCallback callback) {
            requestPermissions(Permissions.LOCATION, callback);
        }
        
        private String[] getPermissionsToRequest(String[] permissions) {
            List<String> permissionsToRequest = new ArrayList<>();
            Context context = fragment.requireContext();
            
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) 
                        != PackageManager.PERMISSION_GRANTED) {
                    
                    // For Android 10+, skip WRITE_EXTERNAL_STORAGE as it's not needed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && 
                        Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                        continue;
                    }
                    
                    permissionsToRequest.add(permission);
                }
            }
            
            return permissionsToRequest.toArray(new String[0]);
        }
        
        private void handlePermissionResult(Map<String, Boolean> result) {
            if (currentCallback == null) return;
            
            List<String> deniedPermissions = new ArrayList<>();
            List<String> permanentlyDeniedPermissions = new ArrayList<>();
            
            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                if (!entry.getValue()) {
                    String permission = entry.getKey();
                    deniedPermissions.add(permission);
                    
                    // Check if permission is permanently denied
                    if (!fragment.shouldShowRequestPermissionRationale(permission)) {
                        permanentlyDeniedPermissions.add(permission);
                    }
                }
            }
            
            if (deniedPermissions.isEmpty()) {
                currentCallback.onPermissionsGranted();
            } else if (!permanentlyDeniedPermissions.isEmpty()) {
                currentCallback.onPermissionsPermanentlyDenied(permanentlyDeniedPermissions);
            } else {
                currentCallback.onPermissionsDenied(deniedPermissions);
            }
            
            currentCallback = null;
        }
    }
    
    /**
     * Checks if all specified permissions are granted
     * @param context The context to check permissions against
     * @param permissions Array of permissions to check
     * @return true if all permissions are granted, false otherwise
     */
    public static boolean hasPermissions(Context context, String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if a single permission is granted
     * @param context The context to check permission against
     * @param permission The permission to check
     * @return true if permission is granted, false otherwise
     */
    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Checks if storage permissions are needed for the current Android version
     * @return true if storage permissions are needed, false otherwise
     */
    public static boolean needsStoragePermissions() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
    }
    
    /**
     * Gets a user-friendly description for a permission
     * @param permission The permission to get description for
     * @return User-friendly description
     */
    public static String getPermissionDescription(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return "Depolama okuma izni";
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return "Depolama yazma izni";
            case Manifest.permission.CAMERA:
                return "Kamera izni";
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return "Hassas konum izni";
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return "Yaklaşık konum izni";
            case Manifest.permission.READ_CONTACTS:
                return "Kişiler okuma izni";
            case Manifest.permission.WRITE_CONTACTS:
                return "Kişiler yazma izni";
            case Manifest.permission.READ_PHONE_STATE:
                return "Telefon durumu okuma izni";
            case Manifest.permission.CALL_PHONE:
                return "Telefon arama izni";
            default:
                return "Bilinmeyen izin";
        }
    }
    
    /**
     * Creates a comma-separated string of permission descriptions
     * @param permissions Array of permissions
     * @return Formatted permission descriptions string
     */
    public static String getPermissionsDescription(String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < permissions.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getPermissionDescription(permissions[i]));
        }
        
        return sb.toString();
    }
} 