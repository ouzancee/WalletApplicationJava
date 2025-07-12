package com.example.walletapplication.presentation.util;

import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * Example usage patterns for PermissionUtil
 * This class demonstrates different ways to use the PermissionUtil in your fragments
 * 
 * NOTE: This is an example/documentation class, not meant to be instantiated
 */
public class PermissionUsageExample {
    
    /**
     * Example Fragment showing different permission usage patterns
     */
    public static class ExampleFragment extends Fragment {
        
        private PermissionUtil.PermissionManager permissionManager;
        
        // Initialize permission manager in your fragment
        private void initializePermissionManager() {
            permissionManager = new PermissionUtil.PermissionManager(this);
        }
        
        // EXAMPLE 1: Simple storage permission request
        private void requestStoragePermissionExample() {
            permissionManager.requestStoragePermissions(new PermissionUtil.PermissionCallback() {
                @Override
                public void onPermissionsGranted() {
                    // Permission granted, proceed with file operations
                    saveFileToExternalStorage();
                }
                
                @Override
                public void onPermissionsDenied(List<String> deniedPermissions) {
                    // Show user-friendly error message
                    showError("Depolama izni gerekli");
                }
            });
        }
        
        // EXAMPLE 2: Camera permission request
        private void requestCameraPermissionExample() {
            permissionManager.requestCameraPermissions(new PermissionUtil.PermissionCallback() {
                @Override
                public void onPermissionsGranted() {
                    // Open camera
                    openCamera();
                }
                
                @Override
                public void onPermissionsDenied(List<String> deniedPermissions) {
                    showError("Kamera izni reddedildi");
                }
                
                @Override
                public void onPermissionsPermanentlyDenied(List<String> permanentlyDeniedPermissions) {
                    showError("Kamera izni kalıcı olarak reddedildi. Lütfen ayarlardan etkinleştirin.");
                }
            });
        }
        
        // EXAMPLE 3: Custom permission array
        private void requestCustomPermissionsExample() {
            String[] customPermissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CAMERA
            };
            
            permissionManager.requestPermissions(customPermissions, new PermissionUtil.PermissionCallback() {
                @Override
                public void onPermissionsGranted() {
                    // All permissions granted
                    startLocationBasedCameraFeature();
                }
                
                @Override
                public void onPermissionsDenied(List<String> deniedPermissions) {
                    String deniedText = PermissionUtil.getPermissionsDescription(
                        deniedPermissions.toArray(new String[0])
                    );
                    showError("Gerekli izinler verilmedi: " + deniedText);
                }
            });
        }
        
        // EXAMPLE 4: Using predefined permission groups
        private void requestPredefinedPermissionsExample() {
            // Location permissions
            permissionManager.requestPermissions(
                PermissionUtil.Permissions.LOCATION, 
                createLocationPermissionCallback()
            );
            
            // Or contacts permissions
            permissionManager.requestPermissions(
                PermissionUtil.Permissions.CONTACTS, 
                createContactsPermissionCallback()
            );
        }
        
        // EXAMPLE 5: Check permissions before requesting
        private void checkPermissionsExample() {
            if (permissionManager.hasPermissions(PermissionUtil.Permissions.STORAGE)) {
                // Already have storage permissions
                saveFileToExternalStorage();
            } else {
                // Need to request permissions
                permissionManager.requestStoragePermissions(createStorageCallback());
            }
        }
        
        // EXAMPLE 6: Static utility methods
        private void staticUtilityMethodsExample() {
            // Check if storage permissions are needed for current Android version
            if (PermissionUtil.needsStoragePermissions()) {
                // Need to check permissions on older Android versions
                requestStoragePermissions();
            } else {
                // Android 10+, can proceed directly
                saveFileToExternalStorage();
            }
            
            // Check single permission
            boolean hasCameraPermission = PermissionUtil.hasPermission(
                requireContext(), 
                android.Manifest.permission.CAMERA
            );
            
            if (hasCameraPermission) {
                openCamera();
            }
        }
        
        // Helper callback creators
        private PermissionUtil.PermissionCallback createStorageCallback() {
            return new PermissionUtil.PermissionCallback() {
                @Override
                public void onPermissionsGranted() {
                    saveFileToExternalStorage();
                }
                
                @Override
                public void onPermissionsDenied(List<String> deniedPermissions) {
                    showError("Depolama izni gerekli");
                }
            };
        }
        
        private PermissionUtil.PermissionCallback createLocationPermissionCallback() {
            return new PermissionUtil.PermissionCallback() {
                @Override
                public void onPermissionsGranted() {
                    startLocationServices();
                }
                
                @Override
                public void onPermissionsDenied(List<String> deniedPermissions) {
                    showError("Konum izni gerekli");
                }
            };
        }
        
        private PermissionUtil.PermissionCallback createContactsPermissionCallback() {
            return new PermissionUtil.PermissionCallback() {
                @Override
                public void onPermissionsGranted() {
                    accessContacts();
                }
                
                @Override
                public void onPermissionsDenied(List<String> deniedPermissions) {
                    showError("Kişiler izni gerekli");
                }
            };
        }
        
        // Dummy implementation methods (replace with your actual implementation)
        private void saveFileToExternalStorage() { /* Your implementation */ }
        private void openCamera() { /* Your implementation */ }
        private void startLocationBasedCameraFeature() { /* Your implementation */ }
        private void requestStoragePermissions() { /* Your implementation */ }
        private void startLocationServices() { /* Your implementation */ }
        private void accessContacts() { /* Your implementation */ }
        private void showError(String message) { /* Your implementation */ }
    }
    
    /**
     * INTEGRATION CHECKLIST:
     * 
     * 1. CREATE PERMISSION MANAGER:
     *    permissionManager = new PermissionUtil.PermissionManager(this);
     * 
     * 2. CHOOSE YOUR METHOD:
     *    - requestStoragePermissions() for storage
     *    - requestCameraPermissions() for camera  
     *    - requestLocationPermissions() for location
     *    - requestPermissions(customArray) for custom permissions
     * 
     * 3. IMPLEMENT CALLBACK:
     *    - onPermissionsGranted() - proceed with your feature
     *    - onPermissionsDenied() - show error message
     *    - onPermissionsPermanentlyDenied() - guide user to settings
     * 
     * 4. OPTIONAL OPTIMIZATIONS:
     *    - Use hasPermissions() to check before requesting
     *    - Use static utility methods for version checks
     *    - Use getPermissionsDescription() for user-friendly messages
     * 
     * BENEFITS:
     * ✅ Clean, reusable code
     * ✅ Handles Android version differences automatically
     * ✅ User-friendly error messages
     * ✅ Consistent permission handling across app
     * ✅ Easy to maintain and extend
     */
} 